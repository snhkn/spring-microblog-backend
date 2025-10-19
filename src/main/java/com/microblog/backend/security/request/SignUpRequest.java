package com.microblog.backend.security.request;

import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {

    private String username;

    private String email;

    private String password;

}