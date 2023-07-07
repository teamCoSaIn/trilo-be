package com.cosain.trilo.user.domain;

import com.cosain.trilo.auth.infra.OAuthProfileDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "users")
@ToString(of = {"id", "name", "email", "profileImageURL", "authProvider", "role", "myPageImage"})
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

    @Column(nullable = false)
    @Embedded
    private Image myPageImage;

    @Builder(access = AccessLevel.PUBLIC)
    private User(Long id,String name, String email, String profileImageUrl, AuthProvider authProvider, Role role, String myPageImageBaseURL) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImageURL = profileImageUrl;
        this.authProvider = authProvider;
        this.role = role;
        this.myPageImage = Image.initializeMyPageImage(myPageImageBaseURL);
    }

    public static User from(OAuthProfileDto oAuthProfileDto, String myPageImageBaseURL) {
        return User.builder()
                .name(oAuthProfileDto.getName())
                .email(oAuthProfileDto.getEmail())
                .profileImageUrl(oAuthProfileDto.getProfileImageUrl())
                .role(Role.MEMBER)
                .authProvider(oAuthProfileDto.getProvider())
                .myPageImageBaseURL(myPageImageBaseURL)
                .build();
    }

    public void updateUserByOauthProfile(OAuthProfileDto oAuthProfileDto) {
        this.name = oAuthProfileDto.getName();
        this.profileImageURL = oAuthProfileDto.getProfileImageUrl();
    }
}
