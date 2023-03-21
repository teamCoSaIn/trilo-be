package com.cosain.trilo.config.security.filter;

import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.common.exception.TokenAuthenticationFilterException;
import com.cosain.trilo.common.exception.UserNotFoundException;
import com.cosain.trilo.config.security.dto.UserPrincipal;
import com.cosain.trilo.config.security.util.HeaderUtil;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenAnalyzer tokenAnalyzer;
    private final UserRepository userRepository;

    public TokenAuthenticationFilter(TokenAnalyzer tokenAnalyzer, UserRepository userRepository){
        this.tokenAnalyzer = tokenAnalyzer;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            if(HeaderUtil.isAuthorizationHeaderExist(request)){
                String authToken = HeaderUtil.getAuthTokenFrom(request);
                tokenAnalyzer.validateToken(authToken);
                String email = tokenAnalyzer.getEmailFromToken(authToken);
                User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
                storeUserInSecurityContext(user, request);
            }

        }catch(Exception cause){
            throw new TokenAuthenticationFilterException(cause);
        }

        filterChain.doFilter(request, response);
    }

    private void storeUserInSecurityContext(User user, HttpServletRequest request){

        UserPrincipal userPrincipal = UserPrincipal.from(user);

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
