package com.microblog.backend.service;

import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.PostDTO;

import java.security.Principal;
import java.util.List;

public interface PostService {
    List<Post> getAllPosts();

    Post createPost(Post post);

    Post editPost(Long postId, String body, SocialUser user);

    List<Post> getCurrentUserPosts(String email);

    List<Post> getPostsByUserId(Long userId);
}
