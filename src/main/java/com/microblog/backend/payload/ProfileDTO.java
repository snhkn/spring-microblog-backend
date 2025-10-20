package com.microblog.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private String username;
    private String email;
    private String gravatarUrl;
    private LocalDateTime lastseen;

}
