package com.microblog.backend.service;

import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public ProfileServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProfileDTO getProfileByUsername(String username) {
        SocialUser user = userRepository.findByUsername(username)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        return modelMapper.map(user, ProfileDTO.class);
    }
}
