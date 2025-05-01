package org.studyeasy.SpringRestdemo.payload.auth.album;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoViewDTO {

    private long id;

    private String name;

    private String description;
    
}
