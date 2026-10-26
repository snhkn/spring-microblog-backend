package com.microblog.backend.controller;

import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.UserDTO;
import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.security.jwt.JwtUtils;
import com.microblog.backend.security.services.CustomUserDetailsService;
import com.microblog.backend.security.services.UserDetailsImpl;
import com.microblog.backend.service.FollowService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc
public class FollowControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    FollowService followService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    ModelMapper modelMapper;

    @MockitoBean
    JwtUtils JwtUtils;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    private UserDetailsImpl mockCurrentUser(Long userId) {
        UserDetailsImpl currentUser = mock(UserDetailsImpl.class);
        when(currentUser.getId()).thenReturn(userId);
        return currentUser;
    }

    @Test
    void followUser_shouldReturnSuccessMessage() throws Exception{

        UserDetailsImpl currentUser = mockCurrentUser(1L);

        mockMvc.perform(post("/api/follow/{followedId}", 2L)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        currentUser, null, List.of()
                                )
                        ))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Followed successfully."));

        verify(followService).followUser(1L, 2L);

    }
    @Test
    void unfollowUser_shouldReturnSuccessMessage() throws Exception {
        UserDetailsImpl currentUser = mockCurrentUser(1L);

        mockMvc.perform(delete("/api/follow/{followedId}", 2L)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        currentUser, null, List.of()
                                )
                        ))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Unfollowed successfully."));

        verify(followService).unfollowUser(1L, 2L);
    }

    @Test
    void getFollowers_shouldReturnFollowerDTOs() throws Exception {
        SocialUser follower = mock(SocialUser.class);
        when(follower.getFollowers()).thenReturn(Set.of());
        when(follower.getFollowing()).thenReturn(Set.of());
        when(follower.getGravatarUrl())
                .thenReturn("https://example.com/avatar.png");

        UserDTO followerDTO = new UserDTO();
        followerDTO.setUsername("Bob");

        when(followService.getFollowers(1L)).thenReturn(Set.of(follower));
        when(modelMapper.map(follower, UserDTO.class)).thenReturn(followerDTO);

        mockMvc.perform(get("/api/follow/followers/{userId}", 1L)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        mockCurrentUser(1L), null, List.of()
                                )
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Bob"))
                .andExpect(jsonPath("$[0].followersCount").value(0))
                .andExpect(jsonPath("$[0].followingCount").value(0))
                .andExpect(jsonPath("$[0].gravatarUrl")
                        .value("https://example.com/avatar.png"));

        verify(followService).getFollowers(1L);
        verify(modelMapper).map(follower, UserDTO.class);
    }

    @Test
    void getFollowing_shouldReturnFollowingDTOs() throws Exception {
        SocialUser following = mock(SocialUser.class);
        when(following.getFollowers()).thenReturn(Set.of());
        when(following.getFollowing()).thenReturn(Set.of());
        when(following.getGravatarUrl()).thenReturn("https://example.com/avatar.png");

        UserDTO followingDTO = new UserDTO();
        followingDTO.setUsername("Charlie");

        when(followService.getFollowing(1L)).thenReturn(Set.of(following));
        when(modelMapper.map(following, UserDTO.class)).thenReturn(followingDTO);

        mockMvc.perform(get("/api/follow/following/{userId}", 1L)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        mockCurrentUser(1L), null, List.of()
                                )
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Charlie"))
                .andExpect(jsonPath("$[0].followersCount").value(0))
                .andExpect(jsonPath("$[0].followingCount").value(0))
                .andExpect(jsonPath("$[0].gravatarUrl")
                        .value("https://example.com/avatar.png"));

        verify(followService).getFollowing(1L);
        verify(modelMapper).map(following, UserDTO.class);
    }

    @Test
    void getFollowers_whenNoFollowers_shouldReturnEmptySet() throws Exception {
        when(followService.getFollowers(1L)).thenReturn(Set.of());

        mockMvc.perform(get("/api/follow/followers/{userId}", 1L)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        mockCurrentUser(1L), null, List.of()
                                )
                        )))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(followService).getFollowers(1L);
        verifyNoInteractions(modelMapper);
    }
}
