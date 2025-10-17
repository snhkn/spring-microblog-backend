package com.microblog.backend.service;

import com.microblog.backend.model.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();

    Post createPost(Post post);
}
