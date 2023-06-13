package com.cosain.trilo.auth.infra.oauth.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverInfoResponse {

    @JsonProperty("response")
    private Response response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Response {
        private String email;
        private String name;
        private String profile_image;
    }

    public String getEmail(){
        return response.getEmail();
    }

    public String getName(){
        return response.getName();
    }

    public String getImageUrl(){
        return response.getProfile_image();
    }
}
