package com.cosain.trilo.config.security.filter;

import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.common.exception.TokenAuthenticationFilterException;
import com.cosain.trilo.common.exception.UserNotFoundException;
import com.cosain.trilo.config.security.dto.UserPrincipal;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenAnalyzer tokenAnalyzer;
    private final UserRepository userRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTH_TYPE = "Bearer ";

    public TokenAuthenticationFilter(TokenAnalyzer tokenAnalyzer, UserRepository userRepository){
        this.tokenAnalyzer = tokenAnalyzer;
        this.userRepository = userRepository;
    }

    /**
     * 토큰 인증 필터에서는 Authorization Header 를 검사하여, 접근 토큰을 추출하고
     * 해당 토큰이 유효하다면 인증 객체를 Security ContextHolder에 저장해서
     * 해당 사용자가 누군지 식별(인증)할 수 있게 합니다.
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String accessToken = getAccessTokenFromRequest(request);
            if(tokenAnalyzer.validateToken(accessToken)){
                String email = tokenAnalyzer.getEmailFromToken(accessToken);
                User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
                storeUserInSecurityContext(user, request);
            }
            log.info("accessToken : {}",accessToken);
        }catch(Exception cause){
            throw new TokenAuthenticationFilterException(cause);
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request){
        String authHeaderValue = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(authHeaderValue) && authHeaderValue.startsWith(AUTH_TYPE)){
            return authHeaderValue.substring(AUTH_TYPE.length());
        }
        return null;
    }

    private void storeUserInSecurityContext(User user, HttpServletRequest request){

        UserPrincipal userPrincipal = UserPrincipal.from(user);

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
