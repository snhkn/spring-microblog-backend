package com.microblog.backend.service;

import com.microblog.backend.payload.ProfileDTO;


public interface ProfileService {

    ProfileDTO getProfileByUserId(Long userId);

    ProfileDTO getMyProfile(String name);

    ProfileDTO updateMyProfile(String name, ProfileDTO profileDTO);
}
