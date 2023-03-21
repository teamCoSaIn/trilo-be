package com.cosain.trilo.config.security.service;

import com.cosain.trilo.config.security.dto.OAuthAttributes;
import com.cosain.trilo.config.security.dto.UserPrincipal;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuthService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth 정보 : " + oAuth2User.getAttributes());
        log.info("accessToken : "+ userRequest.getAccessToken());

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(oAuth2User, userRequest);
        User user = saveOrUpdateUserWithAttributes(oAuthAttributes);

        return UserPrincipal.from(user);
    }

    private User saveOrUpdateUserWithAttributes(OAuthAttributes oAuthAttributes) {
        Optional<User> userOptional = userRepository.findByEmail(oAuthAttributes.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.updateUserByOAuthInfo(oAuthAttributes);
            return user;
        }

        return userRepository.save(User.from(oAuthAttributes));
    }
}
