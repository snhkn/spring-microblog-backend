package com.microblog.backend.controller;

import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.PostDTO;
import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class PostController {

    PostService postService;
    ModelMapper modelMapper;
    UserRepository userRepository;

    public PostController(PostService postService, ModelMapper modelMapper, UserRepository userRepository) {
        this.postService = postService;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @GetMapping("/public/posts")
    public List<PostDTO> getAllPosts(){
        List<Post> posts = postService.getAllPosts();

        return posts.stream().map(post-> {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setAuthor(post.getAuthor().getUsername());
            dto.setBody(post.getBody());
            dto.setCreatedAt(post.getTimestamp().format(formatter));
            dto.setUserId(post.getAuthor().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping("/admin/posts")
    public PostDTO createPost(@RequestBody PostDTO postDTO, Principal principal){
        // Find logged-in user
        SocialUser author = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Map DTO -> Entity
        Post post = modelMapper.map(postDTO, Post.class);
        post.setAuthor(author); // Set the author before saving


        Post createdPost = postService.createPost(post);

        // Build Response DTO
        PostDTO responseDTO = new PostDTO();
        responseDTO.setId(createdPost.getId());
        responseDTO.setBody(createdPost.getBody());
        responseDTO.setCreatedAt(createdPost.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        responseDTO.setAuthor(createdPost.getAuthor().getUsername());
        responseDTO.setUserId(createdPost.getAuthor().getId());

        return responseDTO;
    }
}
