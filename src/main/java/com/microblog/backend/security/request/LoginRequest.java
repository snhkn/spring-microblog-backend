package com.microblog.backend.security.request;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class LoginRequest {

    private String email;

    private String password;

}