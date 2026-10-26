package com.microblog.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.payload.UserSearchDTO;
import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.security.jwt.JwtUtils;
import com.microblog.backend.security.services.CustomUserDetailsService;
import com.microblog.backend.service.PostService;
import com.microblog.backend.service.ProfileService;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    ProfileService profileService;

    @MockitoBean
    PostService postService;

    @MockitoBean
    UserRepository userRepository;



    @Test
    void  getProfile_shouldReturnProfile() throws Exception{
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("Alice");
        profileDTO.setEmail("alice@example.com");
        profileDTO.setAboutMe("about me");
        profileDTO.setGravatarUrl("gravatar-url");
        profileDTO.setLastSeen(LocalDateTime.of(2026, 10, 22, 14, 30));

        when(profileService.getProfileByUserId(1L))
                .thenReturn(profileDTO);

        mockMvc.perform(get("/api/profile/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.aboutMe").value("about me"))
                .andExpect(jsonPath("$.gravatarUrl").value("gravatar-url"))
                .andExpect(jsonPath("$.lastSeen").value("2026-10-22T14:30:00"));

        verify(profileService).getProfileByUserId(1L);
    }

    @Test
    void getMyProfile_shouldReturnProfile() throws Exception{
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("Alice");
        profileDTO.setEmail("alice@example.com");
        profileDTO.setAboutMe("about me");
        profileDTO.setGravatarUrl("gravatar-url");
        profileDTO.setLastSeen(LocalDateTime.of(2026, 10, 22, 14, 30));

        when(profileService.getMyProfile("alice@example.com")).thenReturn(profileDTO);

        mockMvc.perform(get("/api/profile/users/me")
                .principal(() -> "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.aboutMe").value("about me"))
                .andExpect(jsonPath("$.gravatarUrl").value("gravatar-url"))
                .andExpect(jsonPath("$.lastSeen").value("2026-10-22T14:30:00"));

        verify(profileService).getMyProfile("alice@example.com");

    }

    @Test
    void updateMyProfile_shouldUpdateAndReturnProfile() throws Exception{

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("Alice");
        profileDTO.setEmail("alice@example.com");
        profileDTO.setAboutMe("about me");
        profileDTO.setGravatarUrl("gravatar-url");
        profileDTO.setLastSeen(LocalDateTime.of(2026, 10, 22, 14, 30));

        ProfileDTO requestDTO = new ProfileDTO();
        requestDTO.setAboutMe("Edited about me");

        ProfileDTO editedProfileDTO = new ProfileDTO();
        editedProfileDTO.setUsername("Alice");
        editedProfileDTO.setEmail("alice@example.com");
        editedProfileDTO.setAboutMe("Edited about me");
        editedProfileDTO.setGravatarUrl("gravatar-url");
        editedProfileDTO.setLastSeen(LocalDateTime.of(2026, 10, 22, 14, 30));

        when(profileService.updateMyProfile(
                eq("alice@example.com"),
                any(ProfileDTO.class)
        )).thenReturn(editedProfileDTO);

        mockMvc.perform(put("/api/profile/users/me")
                        .principal(() -> "alice@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.aboutMe").value("Edited about me"))
                .andExpect(jsonPath("$.gravatarUrl").value("gravatar-url"))
                .andExpect(jsonPath("$.lastSeen").value("2026-10-22T14:30:00"));

        verify(profileService).updateMyProfile(eq("alice@example.com"),
                any(ProfileDTO.class));
        verify(profileService).updateMyProfile(
                eq("alice@example.com"),
                argThat(dto -> "Edited about me".equals(dto.getAboutMe()))
        );

    }

    @Test
    void getCurrentUserPosts_shouldReturnPosts() throws Exception {
        SocialUser author = new SocialUser();
        author.setId(1L);
        author.setUsername("Alice");

        Post post = new Post();
        post.setId(10L);
        post.setAuthor(author);
        post.setBody("My first post");
        post.setTimestamp(LocalDateTime.of(2026, 10, 22, 14, 30));

        when(postService.getCurrentUserPosts("alice@example.com"))
                .thenReturn(List.of(post));

        mockMvc.perform(get("/api/profile/users/me/posts")
                        .principal(() -> "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].author").value("Alice"))
                .andExpect(jsonPath("$[0].body").value("My first post"))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-10-22 14:30"));

        verify(postService).getCurrentUserPosts("alice@example.com");
    }

    @Test
    void getUserPosts_shouldReturnPosts() throws Exception {
        SocialUser author = new SocialUser();
        author.setId(1L);
        author.setUsername("Alice");

        Post post = new Post();
        post.setId(10L);
        post.setAuthor(author);
        post.setBody("Alice's post");
        post.setTimestamp(LocalDateTime.of(2026, 10, 22, 14, 30));

        when(postService.getPostsByUserId(1L))
                .thenReturn(List.of(post));

        mockMvc.perform(get("/api/profile/users/{userId}/posts", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].author").value("Alice"))
                .andExpect(jsonPath("$[0].body").value("Alice's post"))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-10-22 14:30"));

        verify(postService).getPostsByUserId(1L);
    }

    @Test
    void searchUsers_shouldTrimQueryAndReturnResults() throws Exception {
        UserSearchDTO user = new UserSearchDTO();
        user.setUsername("Alice");

        when(profileService.searchUsers("ali"))
                .thenReturn(List.of(user));

        mockMvc.perform(get("/api/profile/users/search")
                        .param("query", "  ali  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Alice"));

        verify(profileService).searchUsers("ali");
    }

}
