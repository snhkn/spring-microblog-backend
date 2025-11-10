package com.microblog.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.DigestUtils;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email")
        })
public class SocialUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    private String aboutMe;

    @OneToMany(mappedBy = "author")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Post> posts;

    @ManyToMany
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SocialUser> following = new HashSet<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SocialUser> followers = new HashSet<>();

    // --- Helper methods ---
    public void follow(SocialUser user) {
        if (!this.equals(user)) {
            this.following.add(user);
            user.getFollowers().add(this);
        }
    }

    public void unfollow(SocialUser user) {
        if (!this.equals(user)) {
            // Defensive copy
            Set<SocialUser> updatedFollowing = new HashSet<>(this.following);
            updatedFollowing.remove(user);
            this.following.clear();
            this.following.addAll(updatedFollowing);

            // Also remove this user from the target's followers
            user.getFollowers().remove(this);
        };
    }


    public boolean isFollowing(SocialUser user) {
        return following.contains(user);
    }

    public String getGravatarUrl() {
        String hash = DigestUtils.md5DigestAsHex(email.trim().toLowerCase().getBytes());
        return "https://www.gravatar.com/avatar/" + hash + "?s=200&d=identicon";
    }
}
