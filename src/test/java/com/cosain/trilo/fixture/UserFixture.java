package com.cosain.trilo.fixture;

import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;

public enum UserFixture {

    KAKAO_MEMBER(1L,"김개똥", "asjoeifjlaksd@nate.com", AuthProvider.KAKAO, Role.MEMBER, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"),
    GOOGLE_MEMBER(2L,"김기상", "slkdjvlakjsdvl@gmail.com", AuthProvider.GOOGLE, Role.MEMBER, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"),
    NAVER_MEMBER(3L,"김미나", "sfsiejfoiseffl@naver.com", AuthProvider.NAVER, Role.MEMBER, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"),
    ;

    private final Long id;
    private final String name;
    private final String email;
    private final AuthProvider authProvider;
    private final Role role;
    private final String profileImageUrl;


    UserFixture(Long id,String name, String email, AuthProvider authProvider, Role role, String profileImageUrl){
        this.id = id;
        this.name = name;
        this.email = email;
        this.authProvider = authProvider;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
    }

    public User create(){
        return User.builder()
                .id(id)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .email(email)
                .authProvider(authProvider)
                .role(role)
                .build();
    }
}
