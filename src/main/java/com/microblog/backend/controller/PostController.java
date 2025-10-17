package com.microblog.backend.controller;

import com.microblog.backend.model.Post;
import com.microblog.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping("/public/posts")
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }

    @PostMapping("/admin/posts")
    public Post createPost(@RequestBody Post post){
        return postService.createPost(post);
    }
}
