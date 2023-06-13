package com.cosain.trilo.auth.application.dto;

import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    MultiValueMap<String, String> getParams();
}
