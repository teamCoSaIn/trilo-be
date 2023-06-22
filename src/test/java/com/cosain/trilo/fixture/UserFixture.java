package com.cosain.trilo.fixture;

import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;

public enum UserFixture {

    KAKAO_MEMBER(1L,"김개똥", "asjoeifjlaksd@nate.com", AuthProvider.KAKAO),
    GOOGLE_MEMBER(2L,"김기상", "slkdjvlakjsdvl@gmail.com", AuthProvider.GOOGLE),
    NAVER_MEMBER(3L,"김미나", "sfsiejfoiseffl@naver.com", AuthProvider.NAVER),
    ;

    private final Long id;
    private final String name;
    private final String email;
    private final AuthProvider authProvider;

    UserFixture(Long id,String name, String email, AuthProvider authProvider){
        this.id = id;
        this.name = name;
        this.email = email;
        this.authProvider = authProvider;
    }

    public User create(){
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .authProvider(authProvider)
                .role(Role.MEMBER)
                .build();
    }
}
