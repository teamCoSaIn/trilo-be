package com.cosain.trilo.fixture;

import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.MyPageImage;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;

public class UserFixture {

    public static User kakaoUser_Id(Long userId) {
        return createUser(userId, "kakao-user", "kakaouser@kakao.com",AuthProvider.KAKAO);
    }

    public static User kakaoUser_NullId() {
        return kakaoUser_Id(null);
    }

    public static User naverUser_Id(Long userId) {
        return createUser(userId, "naver-user", "naveruser@naver.com", AuthProvider.NAVER);
    }

    public static User naverUser_NullId() {
        return naverUser_Id(null);
    }

    public static User googleUser_Id(Long userId) {
        return createUser(userId, "google-user", "googleuser@gmail.com", AuthProvider.GOOGLE);
    }

    public static User googleUser_NullId() {
        return googleUser_Id(null);
    }

    private static User createUser(Long userId, String nickName, String email, AuthProvider authProvider) {
        return User.builder()
                .id(userId)
                .nickName(nickName)
                .profileImageUrl("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .email(email)
                .authProvider(authProvider)
                .role(Role.MEMBER)
                .myPageImage(MyPageImage.initializeMyPageImage())
                .isDeleted(false)
                .build();
    }
}
