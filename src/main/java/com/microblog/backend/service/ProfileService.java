package com.microblog.backend.service;

import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.payload.UserSearchDTO;

import java.util.List;


public interface ProfileService {

    ProfileDTO getProfileByUserId(Long userId);

    ProfileDTO getMyProfile(String name);

    ProfileDTO updateMyProfile(String name, ProfileDTO profileDTO);

    List<UserSearchDTO> searchUsers(String query);
}
