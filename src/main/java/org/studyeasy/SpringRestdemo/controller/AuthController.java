package org.studyeasy.SpringRestdemo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.payload.auth.AccountDTO;
import org.studyeasy.SpringRestdemo.payload.auth.AccountViewDTO;
import org.studyeasy.SpringRestdemo.payload.auth.AuthoritiesDTO;
import org.studyeasy.SpringRestdemo.payload.auth.PasswordDTO;
import org.studyeasy.SpringRestdemo.payload.auth.ProfileDTO;
import org.studyeasy.SpringRestdemo.payload.auth.TokenDTO;
import org.studyeasy.SpringRestdemo.payload.auth.UserLoginDTO;
import org.studyeasy.SpringRestdemo.service.AccountService;
import org.studyeasy.SpringRestdemo.service.TokenService;
import org.studyeasy.SpringRestdemo.util.constants.AccountError;
import org.studyeasy.SpringRestdemo.util.constants.AccountSuccess;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@Tag(name = "Auth Controller", description = "Controller for Account management")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/token")
    public ResponseEntity<?> token(@Valid @RequestBody UserLoginDTO userLogin) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

            String token = tokenService.generateToken(authentication);
            return ResponseEntity.ok(new TokenDTO(token));
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", userLogin.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            log.error("Unexpected error during token generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
        }
    }

    @PostMapping(value = "/users/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400", description = "Please enter a valid email and password length between 6 to 20 characters")
    @ApiResponse(responseCode = "201", description = "Account added")
    @Operation(summary = "Add a new User")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = new Account();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword()); // raw password only
            accountService.save(account); // encoding handled in service
            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());
        } catch (Exception e) {
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/users", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of users")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token Error")
    @Operation(summary = "List user api")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public List<AccountViewDTO> Users() {
        List<AccountViewDTO> accounts = new ArrayList<>();
        for (Account account : accountService.findAll()) {
            accounts.add(new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities()));
        }
        return accounts;
    }


    @PutMapping(value = "/users/{user_id}/update-authorities/", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Update authorities")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "400", description = "Invalid user")
    @ApiResponse(responseCode = "403", description = "Token Error")
    @Operation(summary = "Update profile")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity <AccountViewDTO> update_auth(@Valid @RequestBody AuthoritiesDTO authoritiesDTO, Authentication authentication, @PathVariable long user_id) {     
            Optional<Account> optionalAccount = accountService.findById(user_id);
            if(optionalAccount.isPresent()){
                Account account = optionalAccount.get();
                account.setAuthorities(authoritiesDTO.getAuthorities());
                accountService.save(account);
                AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities());
                return ResponseEntity.ok(accountViewDTO);
            }
            return new ResponseEntity<AccountViewDTO>(new AccountViewDTO(), HttpStatus.BAD_REQUEST);
        }


    @GetMapping(value = "/profile", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "View profile")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token Error")
    @Operation(summary = "View profile")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ProfileDTO profile(Authentication authentication) {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
                Account account = optionalAccount.get();
                ProfileDTO profileDTO = new ProfileDTO(account.getId(), account.getEmail(), account.getAuthorities());
                return profileDTO;
        }

    @PutMapping(value = "/profile/update-password", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Update profile")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token Error")
    @Operation(summary = "Update profile")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public AccountViewDTO update_password(@Valid @RequestBody PasswordDTO passwordDTO, Authentication authentication) {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
                Account account = optionalAccount.get();
                account.setPassword(passwordDTO.getPassword());
                accountService.save(account);
                AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities());
                return accountViewDTO;
        }

    
        @DeleteMapping(value = "/profile/delete")
        @ApiResponse(responseCode = "200", description = "Update profile")
        @ApiResponse(responseCode = "401", description = "Token missing")
        @ApiResponse(responseCode = "403", description = "Token Error")
        @Operation(summary = "Delete profile")
        @SecurityRequirement(name = "studyeasy-demo-api")
        public ResponseEntity<String> delete_profile(Authentication authentication) {
                String email = authentication.getName();
                Optional<Account> optionalAccount = accountService.findByEmail(email);
                if(optionalAccount.isPresent()){
                    accountService.deleteById(optionalAccount.get().getId());
                    return ResponseEntity.ok("User deleted");
                }
                    return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);
            }


}
