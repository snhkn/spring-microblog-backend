package com.microblog.backend.controller;


import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.UserDTO;
import com.microblog.backend.security.services.UserDetailsImpl;
import com.microblog.backend.service.FollowService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@RequestMapping("/api/follow")
@RestController
public class FollowController {

    FollowService followService;
    ModelMapper modelMapper;

    public FollowController(FollowService followService, ModelMapper modelMapper){
        this.followService = followService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/{followedId}")
    public ResponseEntity<String> followUser(@PathVariable Long followedId,
                                             @AuthenticationPrincipal UserDetailsImpl currentUser){
        followService.followUser(currentUser.getId(), followedId);
        return ResponseEntity.ok("Followed successfully.");
    }

    @DeleteMapping("/{followedId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long followedId,
                                             @AuthenticationPrincipal UserDetailsImpl currentUser){
        followService.unfollowUser(currentUser.getId(), followedId);
        return ResponseEntity.ok("Unfollowed successfully.");
    }


    @GetMapping("/followers/{userId}")
    public ResponseEntity<Set<UserDTO>> getFollowers(@PathVariable Long userId) {
        Set<SocialUser> followers = followService.getFollowers(userId);

        // Defensive copy to prevent concurrent modification
        Set<SocialUser> safeFollowers = new HashSet<>(followers);

        Set<UserDTO> followerDTOs = safeFollowers.stream()
                .map(follower -> {
                    UserDTO dto = modelMapper.map(follower, UserDTO.class);

                    // Make copies before accessing size()
                    dto.setFollowersCount(new HashSet<>(follower.getFollowers()).size());
                    dto.setFollowingCount(new HashSet<>(follower.getFollowing()).size());
                    dto.setGravatarUrl(follower.getGravatarUrl());

                    return dto;
                })
                .collect(Collectors.toSet());

        return ResponseEntity.ok(followerDTOs);
    }



    @GetMapping("/following/{userId}")
    public ResponseEntity<Set<UserDTO>> getFollowing(@PathVariable Long userId) {
        Set<SocialUser> followings = followService.getFollowing(userId);
        Set<SocialUser> safeFollowings = new HashSet<>(followings);

        Set<UserDTO> followingDTOs = safeFollowings.stream()
                .map(following -> {
                    UserDTO dto = modelMapper.map(following, UserDTO.class);

                    dto.setFollowersCount(new HashSet<>(following.getFollowers()).size());
                    dto.setFollowingCount(new HashSet<>(following.getFollowing()).size());
                    dto.setGravatarUrl(following.getGravatarUrl());

                    return dto;
                })
                .collect(Collectors.toSet());

        return ResponseEntity.ok(followingDTOs);
    }

}
