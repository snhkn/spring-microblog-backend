package com.microblog.backend.repositories;

import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.PostDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorOrderByTimestampDesc(SocialUser author);
}
