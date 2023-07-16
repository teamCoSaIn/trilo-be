package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.common.exception.auth.TokenInvalidFormatException;
import com.cosain.trilo.common.exception.auth.TokenNotExistException;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenExtractor {
    public String extractToken(String authorizationHeader, String tokenType){
        if(authorizationHeader == null){
            throw new TokenNotExistException();
        }

        String[] splits = authorizationHeader.split(" ");
        if(splits.length != 2 || !splits[0].equalsIgnoreCase(tokenType)){
            throw new TokenInvalidFormatException();
        }
        return splits[1];
    }
}
