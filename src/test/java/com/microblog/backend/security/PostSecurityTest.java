package com.microblog.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microblog.backend.controller.PostController;
import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.security.jwt.JwtUtils;
import com.microblog.backend.security.services.CustomUserDetailsService;
import com.microblog.backend.service.PostService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostSecurityTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    PostService postService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    ModelMapper modelMapper;

    private static final String VALID_POST_JSON =
            "{\"body\":\"A test post\"}";

    @Test
    void unauthenticatedUserCannotCreatePost() throws Exception {
        mockMvc.perform(post("/admin/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_POST_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedUserCannotEditPost() throws Exception {
        mockMvc.perform(put("/admin/posts/{postId}", 10L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_POST_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedUserCannotDeletePost() throws Exception {
        mockMvc.perform(delete("/admin/posts/{postId}", 10L)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
    

}
