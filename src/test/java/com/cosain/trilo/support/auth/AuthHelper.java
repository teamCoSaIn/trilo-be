package com.cosain.trilo.support.auth;

import com.cosain.trilo.config.security.dto.UserPrincipal;
import com.cosain.trilo.user.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;

public class AuthHelper {

    public static Authentication createAuthentication(User user){
        
        List<SimpleGrantedAuthority> authorityList = Arrays.asList(new SimpleGrantedAuthority(user.getRole().toString()));
        UserPrincipal principal = UserPrincipal.from(user);
        return new UsernamePasswordAuthenticationToken(principal, "", authorityList);
    }
}
