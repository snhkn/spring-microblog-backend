package com.microblog.backend.controller;

import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.PostDTO;
import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.service.PostService;
import com.microblog.backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final PostService postService;

    public ProfileController(ProfileService profileService, PostService postService) {

        this.profileService = profileService;
        this.postService = postService;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @GetMapping("/users/{username}")
    public ProfileDTO getProfile(@PathVariable String username){
        return profileService.getProfileByUsername(username);
    }

    @GetMapping("/users/me")
    public ProfileDTO getMyProfile(Principal principal){
        return profileService.getMyProfile(principal.getName());
    }

    @PostMapping("/users/me")
    public ProfileDTO updateMyProfile(@RequestBody ProfileDTO profileDTO, Principal principal){
        return profileService.updateMyProfile(principal.getName(), profileDTO);
    }

    @GetMapping("/users/me/posts")
    public List<PostDTO> getCurrentUserPosts(Principal principal) {
        System.out.println(principal.getName());
        List<Post> posts =  postService.getCurrentUserPosts(principal.getName());
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



}
