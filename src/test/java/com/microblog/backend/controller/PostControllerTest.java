package com.microblog.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.PostDTO;
import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.security.jwt.JwtUtils;
import com.microblog.backend.security.services.CustomUserDetailsService;
import com.microblog.backend.service.PostService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

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

    @Test
    void getAllPosts_shouldReturnPosts() throws Exception{

        SocialUser user = new SocialUser();
        user.setId(1L);
        user.setUsername("alice");

        Post post = new Post();
        post.setId(10L);
        post.setAuthor(user);
        post.setBody("Hello world");
        post.setTimestamp(LocalDateTime.of(2026, 10, 1, 14, 30 ));

        when(postService.getAllPosts())
                .thenReturn(List.of(post));


        mockMvc.perform(get("/api/public/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].author").value("alice"))
                .andExpect(jsonPath("$[0].body").value("Hello world"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-10-01 14:30"))
                .andExpect(jsonPath("$[0].userId").value(1));
    }


    @Test
    void createPost_shouldCreateAndReturnPost() throws Exception {
        SocialUser user = new SocialUser();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        PostDTO requestDTO = new PostDTO();
        requestDTO.setBody("New post");

        Post mappedPost = new Post();
        mappedPost.setBody("New post");

        Post createdPost = new Post();
        createdPost.setId(10L);
        createdPost.setBody("New post");
        createdPost.setAuthor(user);
        createdPost.setTimestamp(
                LocalDateTime.of(2026, 10, 1, 15, 0)
        );

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(any(PostDTO.class), eq(Post.class)))
                .thenReturn(mappedPost);

        when(postService.createPost(mappedPost))
                .thenReturn(createdPost);

        mockMvc.perform(
                        post("/api/posts")
                                .principal(() -> "alice@example.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.body").value("New post"))
                .andExpect(jsonPath("$.author").value("alice"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-10-01 15:00"));

        verify(userRepository).findByEmail("alice@example.com");
        verify(modelMapper).map(any(PostDTO.class), eq(Post.class));
        verify(postService).createPost(mappedPost);
    }

    @Test
    void editPost_shouldUpdateAndReturnPost() throws Exception {
        SocialUser user = new SocialUser();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        PostDTO requestDTO = new PostDTO();
        requestDTO.setBody("Edited post");

        Post mappedPost = new Post();
        mappedPost.setBody("Edited post");

        Post editedPost = new Post();
        editedPost.setId(10L);
        editedPost.setBody("Edited post");
        editedPost.setAuthor(user);
        editedPost.setTimestamp(
                LocalDateTime.of(2026, 10, 1, 16, 0)
        );

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(any(PostDTO.class), eq(Post.class)))
                .thenReturn(mappedPost);

        when(postService.editPost(
                10L,
                "Edited post",
                user
        )).thenReturn(editedPost);

        mockMvc.perform(
                        put("/api/posts/10")
                                .principal(() -> "alice@example.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.body").value("Edited post"))
                .andExpect(jsonPath("$.author").value("alice"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-10-01 16:00"));

        verify(userRepository).findByEmail("alice@example.com");
        verify(postService).editPost(10L, "Edited post", user);
    }


    @Test
    void deletePost_shouldReturnNoContent() throws Exception {
        SocialUser user = new SocialUser();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(user));

        doNothing().when(postService).deletePost(10L, user);

        mockMvc.perform(
                        delete("/api/posts/10")
                                .principal(() -> "alice@example.com")
                )
                .andExpect(status().isNoContent());

        verify(userRepository).findByEmail("alice@example.com");
        verify(postService).deletePost(10L, user);
    }

}
