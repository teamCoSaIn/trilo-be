package com.cosain.trilo.auth.application;

import com.cosain.trilo.auth.application.dto.OAuthLoginParams;
import com.cosain.trilo.auth.infra.OAuthClient;
import com.cosain.trilo.auth.infra.OAuthProfileDto;
import com.cosain.trilo.user.domain.AuthProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthProfileRequestService {
    private final Map<AuthProvider, OAuthClient> clients;

    public OAuthProfileRequestService(List<OAuthClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthClient::authProvider, Function.identity())
        );
    }

    public OAuthProfileDto request(OAuthLoginParams params){
        OAuthClient client = clients.get(params.authProvider());
        String accessToken = client.getAccessToken(params);
        return client.getProfile(accessToken);
    }
}
