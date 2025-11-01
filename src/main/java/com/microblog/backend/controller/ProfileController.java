package com.microblog.backend.controller;

import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

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



}
