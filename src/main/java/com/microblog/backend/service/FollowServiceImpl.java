package com.microblog.backend.service;

import com.microblog.backend.model.SocialUser;
import com.microblog.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class FollowServiceImpl implements FollowService{

    private final UserRepository userRepository;

    public FollowServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void  followUser(Long followerId, Long followedId){
        SocialUser follower = userRepository.findById(followerId)
                .orElseThrow(()-> new RuntimeException("Follower not found."));
        SocialUser followed = userRepository.findById(followedId)
                .orElseThrow(()-> new RuntimeException("User to follow not found"));
        follower.follow(followed);
        userRepository.save(follower);
    }

    @Transactional
    @Override
    public void unfollowUser(Long followerId, Long followedId) {
        SocialUser follower = userRepository.findById(followerId)
                .orElseThrow(()-> new RuntimeException("Follower not found."));
        SocialUser followed = userRepository.findById(followedId)
                .orElseThrow(()-> new RuntimeException("User to follow not found"));
        follower.unfollow(followed);
        userRepository.save(follower);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<SocialUser> getFollowers(Long userId) {
        SocialUser user = userRepository.findByIdWithFollowers(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // return a detached copy
        return new HashSet<>(user.getFollowers());
    }

    @Transactional(readOnly = true)
    @Override
    public Set<SocialUser> getFollowing(Long userId) {
        SocialUser user = userRepository.findByIdWithFollowing(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new HashSet<>(user.getFollowing());
    }
}
