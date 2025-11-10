package com.microblog.backend.service;

import com.microblog.backend.model.Post;
import com.microblog.backend.model.SocialUser;
import com.microblog.backend.payload.PostDTO;
import com.microblog.backend.repositories.PostRepository;
import com.microblog.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public List<Post> getCurrentUserPosts(String email) {
        SocialUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return postRepository.findByAuthorOrderByTimestampDesc(user);
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        SocialUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findByAuthorOrderByTimestampDesc(user);
    }
}
