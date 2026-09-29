package com.microblog.backend.service;

import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.payload.UserSearchDTO;
import com.microblog.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ProfileServiceImpl profileService;

    @Test
    void getProfileByUserId_shouldReturnProfile(){
        SocialUser user = new SocialUser();

        ProfileDTO profileDTO = new ProfileDTO(
                "alice",
                "alice@example.com",
                null,
                "Hello",
                null
        );


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, ProfileDTO.class)).thenReturn(profileDTO);

        ProfileDTO result = profileService.getProfileByUserId(1L);

        assertSame(profileDTO, result);

        verify(userRepository).findById(1L);
        verify(modelMapper).map(user, ProfileDTO.class);

    }

    @Test
    void getProfileByUserId_shouldThrowNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileService.getProfileByUserId(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(userRepository).findById(1L);
        verify(modelMapper, never()).map(any(), eq(ProfileDTO.class));
    }

    @Test
    void getMyProfile_shouldReturnProfile() {
        SocialUser user = new SocialUser();

        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setAboutMe("Hello");

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(user));

        ProfileDTO result = profileService.getMyProfile("alice@example.com");

        assertEquals("alice", result.getUsername());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals("Hello", result.getAboutMe());

        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void getMyProfile_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> profileService.getMyProfile("alice@example.com")
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void updateMyProfile_shouldUpdateAboutMeAndSave() {
        SocialUser user = new SocialUser();
        user.setEmail("alice@example.com");
        user.setAboutMe("Old about me");

        ProfileDTO dto = new ProfileDTO(
                "alice",
                "alice@example.com",
                null,
                "New about me",
                null
        );

        ProfileDTO updatedProfile = new ProfileDTO(
                "alice",
                "alice@example.com",
                null,
                "New about me",
                null
        );

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(modelMapper.map(user, ProfileDTO.class))
                .thenReturn(updatedProfile);

        ProfileDTO result =
                profileService.updateMyProfile("alice@example.com", dto);

        assertEquals("New about me", user.getAboutMe());
        assertSame(updatedProfile, result);

        verify(userRepository).findByEmail("alice@example.com");
        verify(userRepository).save(user);
        verify(modelMapper).map(user, ProfileDTO.class);
    }
    @Test
    void updateMyProfile_shouldThrowNotFoundWhenUserDoesNotExist() {
        ProfileDTO dto = new ProfileDTO(
                "alice",
                "alice@example.com",
                null,
                "New about me",
                null
        );

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileService.updateMyProfile(
                        "alice@example.com",
                        dto
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(userRepository).findByEmail("alice@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void searchUsers_shouldReturnMatchingUsers() {
        SocialUser alice = new SocialUser();
        alice.setId(1L);
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");

        SocialUser alex = new SocialUser();
        alex.setId(2L);
        alex.setUsername("alex");
        alex.setEmail("alex@example.com");

        when(userRepository.findByUsernameStartingWithIgnoreCase("al"))
                .thenReturn(List.of(alice, alex));

        List<UserSearchDTO> result = profileService.searchUsers("al");

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("alice", result.get(0).getUsername());

        assertEquals(2L, result.get(1).getId());
        assertEquals("alex", result.get(1).getUsername());

        verify(userRepository)
                .findByUsernameStartingWithIgnoreCase("al");
    }

    @Test
    void searchUsers_shouldReturnEmptyListWhenQueryIsBlank() {
        List<UserSearchDTO> result = profileService.searchUsers("   ");

        assertTrue(result.isEmpty());

        verifyNoInteractions(userRepository);
    }

    @Test
    void searchUsers_shouldReturnEmptyListWhenQueryIsNull() {
        List<UserSearchDTO> result = profileService.searchUsers(null);

        assertTrue(result.isEmpty());

        verifyNoInteractions(userRepository);
    }


}

