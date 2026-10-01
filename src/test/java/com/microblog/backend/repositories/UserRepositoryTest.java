package com.microblog.backend.repositories;

import com.microblog.backend.model.SocialUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsernameStartingWithIgnoreCase_shouldReturnMatchingUsers(){
        SocialUser alice = new SocialUser();
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setPassword("password");


        SocialUser alex = new SocialUser();
        alex.setUsername("alex");
        alex.setEmail("alex@example.com");
        alex.setPassword("password");

        SocialUser susan = new SocialUser();
        susan.setUsername("susan");
        susan.setEmail("susan@example.com");
        susan.setPassword("password");

        userRepository.save(alice);
        userRepository.save(alex);
        userRepository.save(susan);

        List<SocialUser> result = userRepository.findByUsernameStartingWithIgnoreCase("AL");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(user -> user.getUsername().equals("alice")));
        assertTrue(result.stream().anyMatch(user -> user.getUsername().equals("alex")));
        assertFalse(result.stream().anyMatch(user -> user.getUsername().equals("susan")));

    }

    @Test
    void findByUsernameStartingWithIgnoreCase_shouldReturnEmptyWhenNoUserMatches() {
        SocialUser alice = new SocialUser();
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setPassword("password");

        userRepository.save(alice);

        List<SocialUser> result =
                userRepository.findByUsernameStartingWithIgnoreCase("xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        SocialUser user = new SocialUser();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("password");

        userRepository.save(user);

        Optional<SocialUser> result =
                userRepository.findByEmail("alice@example.com");

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<SocialUser> result =
                userRepository.findByEmail("missing@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdWithFollowers_shouldReturnUserWithFollowers() {
        SocialUser follower = new SocialUser();
        follower.setUsername("alice");
        follower.setEmail("alice@example.com");
        follower.setPassword("password");

        SocialUser followed = new SocialUser();
        followed.setUsername("bob");
        followed.setEmail("bob@example.com");
        followed.setPassword("password");

        userRepository.save(follower);
        userRepository.save(followed);

        follower.follow(followed);
        userRepository.save(follower);

        Optional<SocialUser> result =
                userRepository.findByIdWithFollowers(followed.getId());

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getFollowers().size());
        assertTrue(
                result.get().getFollowers().stream()
                        .anyMatch(user -> user.getUsername().equals("alice"))
        );
    }

    @Test
    void findByIdWithFollowing_shouldReturnUserWithFollowing() {
        SocialUser follower = new SocialUser();
        follower.setUsername("alice");
        follower.setEmail("alice@example.com");
        follower.setPassword("password");

        SocialUser followed = new SocialUser();
        followed.setUsername("bob");
        followed.setEmail("bob@example.com");
        followed.setPassword("password");

        userRepository.save(follower);
        userRepository.save(followed);

        follower.follow(followed);
        userRepository.save(follower);

        Optional<SocialUser> result =
                userRepository.findByIdWithFollowing(follower.getId());

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getFollowing().size());
        assertTrue(
                result.get().getFollowing().stream()
                        .anyMatch(user -> user.getUsername().equals("bob"))
        );
    }
}
