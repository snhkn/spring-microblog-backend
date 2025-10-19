package com.microblog.backend.controller;

import com.microblog.backend.model.SocialUser;

import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.security.request.LoginRequest;
import com.microblog.backend.security.request.SignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<SocialUser> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password");
        }

        SocialUser user = userOpt.get();
        System.out.println(userOpt);
        System.out.println(user);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password");
        }

        return ResponseEntity.ok("User authenticated successfully!");


    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest){
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        SocialUser newUser = new SocialUser();
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setUsername(signUpRequest.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }

}
