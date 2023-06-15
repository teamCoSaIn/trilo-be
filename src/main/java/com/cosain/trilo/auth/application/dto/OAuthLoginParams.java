package com.cosain.trilo.auth.application.dto;

import com.cosain.trilo.user.domain.AuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    AuthProvider authProvider();
    MultiValueMap<String, String> getParams();
}
