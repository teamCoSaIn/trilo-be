package com.cosain.trilo.auth.infra.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoProfileResponse {

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    public static class KakaoAccount{

        private Profile profile;

        @JsonProperty("email")
        private String email;

        @Getter
        public static class Profile{

            @JsonProperty("nickname")
            private String nickName;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }
}
