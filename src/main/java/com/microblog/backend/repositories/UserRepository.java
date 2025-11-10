package com.microblog.backend.repositories;

import com.microblog.backend.model.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SocialUser, Long> {
    boolean existsByEmail(String email);

    Optional<SocialUser> findByEmail(String email);

    Optional<SocialUser> findByUsername(String username);

    // fetch followers
    @Query("select u from SocialUser u left join fetch u.followers where u.id = :id")
    Optional<SocialUser> findByIdWithFollowers(@Param("id") Long id);

    // fetch following
    @Query("select u from SocialUser u left join fetch u.following where u.id = :id")
    Optional<SocialUser> findByIdWithFollowing(@Param("id") Long id);
}
