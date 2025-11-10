package com.microblog.backend.service;

import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.ProfileDTO;
import com.microblog.backend.repositories.UserRepository;
import com.microblog.backend.security.jwt.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProfileServiceImpl(UserRepository userRepository, ModelMapper modelMapper, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProfileDTO getProfileByUserId(Long userId) {
        SocialUser user = userRepository.findById(userId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        return modelMapper.map(user, ProfileDTO.class);
    }

    @Override
    public ProfileDTO getMyProfile(String email) {
        SocialUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new ProfileDTO(
                user.getUsername(),
                user.getEmail(),
                user.getGravatarUrl(),
                user.getAboutMe(),
                user.getLastSeen()
        );
    }

    @Override
    public ProfileDTO updateMyProfile(String email, ProfileDTO dto) {
        SocialUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setAboutMe(dto.getAboutMe());
        userRepository.save(user);
        return modelMapper.map(user, ProfileDTO.class);
    }
}
