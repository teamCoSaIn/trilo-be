package com.cosain.trilo.fixture;

import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;

public enum UserFixture {

    KAKAO_MEMBER("김개똥", "asjoeifjlaksd@nate.com", AuthProvider.KAKAO),
    GOOGLE_MEMBER("김기상", "slkdjvlakjsdvl@gmail.com", AuthProvider.GOOGLE),
    NAVER_MEMBER("김미나", "sfsiejfoiseffl@naver.com", AuthProvider.NAVER),
    ;

    private final String name;
    private final String email;
    private final AuthProvider authProvider;

    UserFixture(String name, String email, AuthProvider authProvider){
        this.name = name;
        this.email = email;
        this.authProvider = authProvider;
    }

    public User create(){
        return User.builder()
                .name(name)
                .email(email)
                .authProvider(authProvider)
                .role(Role.MEMBER)
                .build();
    }
}
