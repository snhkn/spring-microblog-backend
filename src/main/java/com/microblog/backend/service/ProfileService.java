package com.microblog.backend.service;

import com.microblog.backend.payload.ProfileDTO;


public interface ProfileService {

    ProfileDTO getProfileByUsername(String username);
}
