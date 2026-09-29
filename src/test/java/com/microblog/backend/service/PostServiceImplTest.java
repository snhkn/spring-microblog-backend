package com.microblog.backend.service;

import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.repositories.PostRepository;
import com.microblog.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;


    @Test
    void createPost_shouldSaveAndReturnPost(){
        Post post = new Post();

        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.createPost(post);

        assertSame(post, result);
        verify(postRepository).save(post);
    }

    @Test
    void editPost_shouldUpdatePost_whenUserIsOwner() {
        SocialUser owner = new SocialUser();
        owner.setId(1L);

        Post existingPost = new Post();
        existingPost.setId(10L);
        existingPost.setAuthor(owner);
        existingPost.setBody("Old body");

        Post incomingPost = new Post();
        incomingPost.setId(10L);
        incomingPost.setBody("New body");

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(existingPost));

        when(postRepository.save(existingPost))
                .thenReturn(existingPost);

        Post result = postService.editPost(incomingPost.getId(), incomingPost.getBody(), owner);

        assertEquals("New body", result.getBody());

        verify(postRepository).findById(10L);
        verify(postRepository).save(existingPost);
    }

    @Test
    void editPost_shouldThrowNotFound_whenPostDoesNotExist() {
        SocialUser user = new SocialUser();
        user.setId(1L);

        Post incomingPost = new Post();
        incomingPost.setId(999L);
        incomingPost.setBody("New body");

        when(postRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.editPost(incomingPost.getId(), incomingPost.getBody(), user)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(postRepository).findById(999L);
        verify(postRepository, never()).save(any());
    }

    @Test
    void editPost_shouldThrowForbidden_whenUserIsNotOwner() {
        SocialUser owner = new SocialUser();
        owner.setId(2L);

        SocialUser otherUser = new SocialUser();
        otherUser.setId(1L);

        Post existingPost = new Post();
        existingPost.setId(10L);
        existingPost.setAuthor(owner);
        existingPost.setBody("Alice's post");

        Post incomingPost = new Post();
        incomingPost.setId(10L);
        incomingPost.setBody("Attempted edit");

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(existingPost));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.editPost(incomingPost.getId(), incomingPost.getBody(), otherUser)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(postRepository).findById(10L);
        verify(postRepository, never()).save(any());
    }

    @Test
    void deletePost_shouldDeletePost_whenUserIsOwner(){
        SocialUser owner = new SocialUser();
        owner.setId(1L);

        Post existingPost = new Post();
        existingPost.setId(10L);
        existingPost.setAuthor(owner);

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(existingPost));

        postService.deletePost(10L, owner);

        verify(postRepository).findById(10L);
        verify(postRepository).delete(existingPost);

    }

    @Test
    void deletePost_shouldThrowNotFound_whenPostDoesNotExist() {
        SocialUser user = new SocialUser();
        user.setId(1L);

        when(postRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.deletePost(999L, user)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(postRepository).findById(999L);
        verify(postRepository, never()).delete(any());
    }

    @Test
    void deletePost_shouldThrowForbidden_whenUserIsNotOwner() {
        SocialUser owner = new SocialUser();
        owner.setId(2L);

        SocialUser otherUser = new SocialUser();
        otherUser.setId(1L);

        Post existingPost = new Post();
        existingPost.setId(10L);
        existingPost.setAuthor(owner);

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(existingPost));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.deletePost(10L, otherUser)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(postRepository).findById(10L);
        verify(postRepository, never()).delete(any());
    }


}
