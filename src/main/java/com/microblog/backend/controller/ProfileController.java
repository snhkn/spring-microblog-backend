package com.microblog.backend.controller;

import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public ProfileDTO getProfile(@PathVariable String username){
        return profileService.getProfileByUsername(username);
    }



}
