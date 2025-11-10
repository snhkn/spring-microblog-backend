package com.microblog.backend.service;

import com.microblog.backend.model.Post;
import com.microblog.backend.payload.PostDTO;

import java.security.Principal;
import java.util.List;

public interface PostService {
    List<Post> getAllPosts();

    Post createPost(Post post);

    List<Post> getCurrentUserPosts(String email);

    List<Post> getPostsByUserId(Long userId);
}
