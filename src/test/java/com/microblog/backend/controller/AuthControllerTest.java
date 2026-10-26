package com.microblog.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.security.jwt.JwtUtils;
import com.microblog.backend.security.request.LoginRequest;
import com.microblog.backend.security.request.SignUpRequest;
import com.microblog.backend.security.services.CustomUserDetailsService;
import com.microblog.backend.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    AuthenticationManager authenticationManager;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Test
    void registerUser_shouldRegisterNewUser() throws Exception {

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("Alice");
        signUpRequest.setEmail("alice@example.com");
        signUpRequest.setPassword("password");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed-password");


        mockMvc.perform(
                        post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));


        verify(passwordEncoder).encode("password");
        verify(userRepository).save(argThat(user ->
                "Alice".equals(user.getUsername()) &&
                        "alice@example.com".equals(user.getEmail()) &&
                        "hashed-password".equals(user.getPassword())
        ));
    }

    @Test
    void registerUser_withExistingEmail_shouldReturnError() throws Exception{
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("Alice");
        signUpRequest.setEmail("alice@example.com");
        signUpRequest.setPassword("password");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        mockMvc.perform(
                        post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));

        verify(userRepository).existsByEmail("alice@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(SocialUser.class));

    }

    @Test
    void authenticateUser_withInvalidCredentials_shouldReturnError() throws Exception{
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("alice@example.com");
        loginRequest.setPassword("incorrect-password");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Bad credentials"))
                .andExpect(jsonPath("$.status").value(false));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateUser_withValidCredentials_shouldReturnUserInfoAndCookie() throws Exception{
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("alice@example.com");
        loginRequest.setPassword("password");

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("Alice");
        when(userDetails.getEmail()).thenReturn("alice@example.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails).getAuthorities();

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "test-jwt-token")
                .path("/")
                .httpOnly(true)
                .build();

        when(jwtUtils.generateJwtCookie(userDetails)).thenReturn(jwtCookie);

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, jwtCookie.toString()))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtCookie(userDetails);

    }

    @Test
    void logout_shouldClearJwtCookieAndReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"))
                .andExpect(cookie().exists("jwt"))
                .andExpect(cookie().maxAge("jwt", 0))
                .andExpect(cookie().path("jwt", "/"))
                .andExpect(cookie().httpOnly("jwt", true))
                .andExpect(cookie().secure("jwt", true))
                .andExpect(cookie().domain("jwt", "localhost"));
    }

    @Test
    void logout_withExistingSession_shouldInvalidateSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk());

        assertThat(session.isInvalid()).isTrue();
    }

}
