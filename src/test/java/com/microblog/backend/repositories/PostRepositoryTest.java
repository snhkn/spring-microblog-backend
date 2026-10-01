package com.microblog.backend.repositories;


import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;


    @Test
    void findByOrderByTimestampDesc_shouldReturnPostsNewestFirst() {
        SocialUser user = new SocialUser();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("password");

        userRepository.save(user);

        Post olderPost = new Post();
        olderPost.setBody("Older post");
        olderPost.setAuthor(user);
        olderPost.setTimestamp(
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        Post newerPost = new Post();
        newerPost.setBody("Newer post");
        newerPost.setAuthor(user);
        newerPost.setTimestamp(
                LocalDateTime.of(2026, 1, 2, 10, 0)
        );

        postRepository.save(olderPost);
        postRepository.save(newerPost);

        List<Post> result = postRepository.findByOrderByTimestampDesc();

        assertEquals(2, result.size());
        assertEquals("Newer post", result.get(0).getBody());
        assertEquals("Older post", result.get(1).getBody());
    }


    @Test
    void findByAuthorOrderByTimestampDesc_shouldReturnUsersPostsNewestFirst() {
        SocialUser alice = new SocialUser();
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setPassword("password");

        SocialUser bob = new SocialUser();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setPassword("password");

        userRepository.save(alice);
        userRepository.save(bob);

        Post aliceOlderPost = new Post();
        aliceOlderPost.setBody("Alice older");
        aliceOlderPost.setAuthor(alice);
        aliceOlderPost.setTimestamp(
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        Post aliceNewerPost = new Post();
        aliceNewerPost.setBody("Alice newer");
        aliceNewerPost.setAuthor(alice);
        aliceNewerPost.setTimestamp(
                LocalDateTime.of(2026, 1, 2, 10, 0)
        );

        Post bobPost = new Post();
        bobPost.setBody("Bob post");
        bobPost.setAuthor(bob);
        bobPost.setTimestamp(
                LocalDateTime.of(2026, 1, 3, 10, 0)
        );

        postRepository.save(aliceOlderPost);
        postRepository.save(aliceNewerPost);
        postRepository.save(bobPost);

        List<Post> result =
                postRepository.findByAuthorOrderByTimestampDesc(alice);

        assertEquals(2, result.size());
        assertEquals("Alice newer", result.get(0).getBody());
        assertEquals("Alice older", result.get(1).getBody());

        assertTrue(
                result.stream()
                        .allMatch(post ->
                                post.getAuthor().getId().equals(alice.getId()))
        );
    }
}
