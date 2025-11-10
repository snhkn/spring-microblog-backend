package com.microblog.backend.service;

import com.microblog.backend.model.SocialUser;

import java.util.Set;

public interface FollowService {

    void followUser(Long followerId, Long followedId);

    void unfollowUser(Long followerId, Long followedId);

    Set<SocialUser> getFollowers(Long userId);

    Set<SocialUser> getFollowing(Long userId);
}
