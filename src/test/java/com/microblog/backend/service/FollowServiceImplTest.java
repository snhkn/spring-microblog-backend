package com.microblog.backend.service;

import com.microblog.backend.model.SocialUser;
import com.microblog.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    @Test
    void followUser_shouldFollowAndSave(){
        SocialUser follower = new SocialUser();
        follower.setId(1L);

        SocialUser followed = new SocialUser();
        followed.setId(2L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(follower));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(followed));

        followService.followUser(1L, 2L);

        assertTrue(follower.getFollowing().contains(followed));
        assertTrue(followed.getFollowers().contains(follower));

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(userRepository).save(follower);

    }

    @Test
    void followUser_shouldThrow_whenFollowerDoesNotExist() {
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> followService.followUser(999L, 2L)
        );

        assertEquals("Follower not found.", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void unfollowUser_shouldRemoveFollowingAndSave(){
        SocialUser follower = new SocialUser();
        follower.setId(1L);

        SocialUser followed = new SocialUser();
        followed.setId(2L);

        follower.follow(followed);

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followed));

        followService.unfollowUser(1L, 2L);

        assertFalse(follower.getFollowing().contains(followed));
        assertFalse(followed.getFollowers().contains(follower));

        verify(userRepository).save(follower);

    }

    @Test
    void getFollowers_shouldReturnFollowers(){
        SocialUser user = new SocialUser();
        user.setId(1L);

        SocialUser follower = new SocialUser();
        follower.setId(2L);

        user.getFollowers().add(follower);

        when(userRepository.findByIdWithFollowers(1L)).thenReturn(Optional.of(user));


        Set<SocialUser> result = followService.getFollowers(1L);

        assertEquals(1, result.size());
        assertTrue(result.contains(follower));

        verify(userRepository).findByIdWithFollowers(1L);

    }

    @Test
    void getFollowing_shouldReturnFollowingUsers() {
        SocialUser user = new SocialUser();
        user.setId(1L);

        SocialUser followed = new SocialUser();
        followed.setId(2L);

        user.getFollowing().add(followed);

        when(userRepository.findByIdWithFollowing(1L))
                .thenReturn(Optional.of(user));

        Set<SocialUser> result = followService.getFollowing(1L);

        assertEquals(1, result.size());
        assertTrue(result.contains(followed));

        verify(userRepository).findByIdWithFollowing(1L);
    }
}
