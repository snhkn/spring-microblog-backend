package com.microblog.backend.repositories;

import com.microblog.backend.model.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SocialUser, Long> {
    boolean existsByEmail(String email);

    Optional<SocialUser> findByEmail(String email);

    Optional<SocialUser> findByUsername(String username);
}
