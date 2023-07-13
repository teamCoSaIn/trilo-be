package com.cosain.trilo.user.domain;

import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.user.presentation.dto.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "users")
@ToString(of = {"id", "nickName", "email", "profileImageURL", "authProvider", "role", "myPageImage"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickName;

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
    private MyPageImage myPageImage;

    @Builder(access = AccessLevel.PUBLIC)
    private User(Long id, String nickName, String email, String profileImageUrl, AuthProvider authProvider, Role role) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.profileImageURL = profileImageUrl;
        this.authProvider = authProvider;
        this.role = role;
        this.myPageImage = MyPageImage.initializeMyPageImage();
    }

    public static User from(OAuthProfileDto oAuthProfileDto) {
        return User.builder()
                .nickName(oAuthProfileDto.getName())
                .email(oAuthProfileDto.getEmail())
                .profileImageUrl(oAuthProfileDto.getProfileImageUrl())
                .role(Role.MEMBER)
                .authProvider(oAuthProfileDto.getProvider())
                .build();
    }

    public void updateUserByOauthProfile(OAuthProfileDto oAuthProfileDto) {
        this.nickName = oAuthProfileDto.getName();
        this.profileImageURL = oAuthProfileDto.getProfileImageUrl();
    }

    public void update(UserUpdateRequest userUpdateRequest){
        this.nickName = userUpdateRequest.getNickName();
    }
}
