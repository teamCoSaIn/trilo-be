package com.cosain.trilo.user.domain;

import com.cosain.trilo.config.security.dto.OAuthAttributes;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder(access = AccessLevel.PUBLIC)
    private User(String name, String email, String profileImageUrl, AuthProvider authProvider, Role role) {
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.authProvider = authProvider;
        this.role = role;
    }

    public static User from(OAuthAttributes oAuthAttributes) {
        return User.builder()
                .name(oAuthAttributes.getName())
                .email(oAuthAttributes.getEmail())
                .authProvider(oAuthAttributes.getAuthProvider())
                .profileImageUrl(oAuthAttributes.getProfileImageUrl())
                .role(Role.MEMBER)
                .build();
    }

    public void updateUserByOAuthInfo(OAuthAttributes oAuthAttributes) {
        this.name = oAuthAttributes.getName();
        this.profileImageUrl = oAuthAttributes.getProfileImageUrl();
    }

}
