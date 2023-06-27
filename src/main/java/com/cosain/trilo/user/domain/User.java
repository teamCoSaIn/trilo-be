package com.cosain.trilo.user.domain;

import com.cosain.trilo.auth.infra.OAuthProfileDto;
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
    private String profileImageURL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder(access = AccessLevel.PUBLIC)
    private User(Long id,String name, String email, String profileImageUrl, AuthProvider authProvider, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImageURL = profileImageUrl;
        this.authProvider = authProvider;
        this.role = role;
    }

    public static User from(OAuthProfileDto oAuthProfileDto) {
        return User.builder()
                .name(oAuthProfileDto.getName())
                .email(oAuthProfileDto.getEmail())
                .profileImageUrl(oAuthProfileDto.getProfileImageUrl())
                .role(Role.MEMBER)
                .authProvider(oAuthProfileDto.getProvider())
                .build();
    }

    public void updateUserByOauthProfile(OAuthProfileDto oAuthProfileDto) {
        this.name = oAuthProfileDto.getName();
        this.profileImageURL = oAuthProfileDto.getProfileImageUrl();
    }
}
