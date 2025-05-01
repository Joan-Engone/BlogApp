package org.studyeasy.SpringRestdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.studyeasy.SpringRestdemo.model.Account;
import org.studyeasy.SpringRestdemo.service.AccountService;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Account account01 = new Account();
        Account account02 = new Account();

        account01.setEmail("engonej2@gmail.com");
        account01.setPassword(passwordEncoder.encode("123456"));
        account01.setAuthorities("USER");

        account02.setEmail("engonej3@gmail.com");
        account02.setPassword(passwordEncoder.encode("123456"));
        account02.setAuthorities("ADMIN USER");

        accountService.save(account01);
        accountService.save(account02);
    }
}

