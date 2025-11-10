package com.microblog.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String aboutMe;
    private String gravatarUrl;
    private int followersCount;
    private int followingCount;
}
