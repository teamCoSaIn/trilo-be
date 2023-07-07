package com.cosain.trilo.fixture;

import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Image;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;

public enum UserFixture {

    KAKAO_MEMBER(1L,"김개똥", "asjoeifjlaksd@nate.com", AuthProvider.KAKAO, Role.MEMBER, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg", "https://trilo-images.s3.ap-northeast-2.amazonaws.com/users/defaultBadge.png"),
    GOOGLE_MEMBER(2L,"김기상", "slkdjvlakjsdvl@gmail.com", AuthProvider.GOOGLE, Role.MEMBER, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg", "https://trilo-images.s3.ap-northeast-2.amazonaws.com/users/defaultBadge.png"),
    NAVER_MEMBER(3L,"김미나", "sfsiejfoiseffl@naver.com", AuthProvider.NAVER, Role.MEMBER, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg", "https://trilo-images.s3.ap-northeast-2.amazonaws.com/users/defaultBadge.png"),
    ;

    private final Long id;
    private final String name;
    private final String email;
    private final AuthProvider authProvider;
    private final Role role;
    private final String profileImageURL;
    private final String myPageImageBaseURL;


    UserFixture(Long id,String name, String email, AuthProvider authProvider, Role role, String profileImageURL, String myPageImageBaseURL){
        this.id = id;
        this.name = name;
        this.email = email;
        this.authProvider = authProvider;
        this.role = role;
        this.profileImageURL = profileImageURL;
        this.myPageImageBaseURL = myPageImageBaseURL;
    }

    public User create(){
        return User.builder()
                .id(id)
                .name(name)
                .profileImageUrl(profileImageURL)
                .email(email)
                .authProvider(authProvider)
                .role(role)
                .myPageImageBaseURL(myPageImageBaseURL)
                .build();
    }
}
