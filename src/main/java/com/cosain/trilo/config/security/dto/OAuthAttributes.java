package com.cosain.trilo.config.security.dto;

import com.cosain.trilo.common.exception.NotSupportClientIdException;
import com.cosain.trilo.user.domain.AuthProvider;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String name;
    private String profileImageUrl;
    private String email;
    private AuthProvider authProvider;


    @Builder(access = AccessLevel.PRIVATE)
    private OAuthAttributes(Map<String, Object> attributes, String name, String profileImageUrl, String email, AuthProvider authProvider) {
        this.attributes = attributes;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.authProvider = authProvider;
    }

    public static OAuthAttributes of(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "naver" -> ofNaver(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new NotSupportClientIdException();
        };

    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .authProvider(AuthProvider.GOOGLE)
                .profileImageUrl((String) attributes.get("picture"))
                .attributes(attributes)
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .authProvider(AuthProvider.NAVER)
                .profileImageUrl((String) response.get("profile_image"))
                .attributes(response)
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .authProvider(AuthProvider.KAKAO)
                .profileImageUrl((String) profile.get("profile_image_url"))
                .attributes(kakaoAccount)
                .build();
    }

}
