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
    @Column(name = "user_id")
    private Long id;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageURL;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private MyPageImage myPageImage;

    @Builder(access = AccessLevel.PUBLIC)
    private User(Long id, String nickName, String email, String profileImageUrl, AuthProvider authProvider, Role role, MyPageImage myPageImage) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.profileImageURL = profileImageUrl;
        this.authProvider = authProvider;
        this.role = role;
        this.myPageImage = myPageImage == null ? MyPageImage.initializeMyPageImage() : myPageImage;
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
