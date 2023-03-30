package com.cosain.trilo.config.security;

import com.cosain.trilo.auth.domain.repository.TokenRepository;
import com.cosain.trilo.auth.infra.TokenAnalyzer;
import com.cosain.trilo.config.security.dto.AuthUriResponse;
import com.cosain.trilo.config.security.filter.ExceptionHandlerFilter;
import com.cosain.trilo.config.security.filter.TokenAuthenticationFilter;
import com.cosain.trilo.config.security.handler.CustomAccessDeniedHandler;
import com.cosain.trilo.config.security.handler.CustomAuthenticationEntryPoint;
import com.cosain.trilo.config.security.handler.OAuthSuccessHandler;
import com.cosain.trilo.config.security.service.CustomOAuthService;
import com.cosain.trilo.user.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final CustomOAuthService customOAuthService;
    private final TokenRepository tokenRepository;

    private final ObjectMapper objectMapper;
    private final TokenAnalyzer tokenAnalyzer;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/deploy/**", "/api/auth/token/refresh-token-info").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/reissue").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf().disable()
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> cors
                        .configurationSource(configurationSource())
                )
                .exceptionHandling(handle -> handle
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorize -> authorize
                                .baseUri("/api/auth/login")
                                .authorizationRedirectStrategy(new CustomRedirectStrategy())
                        )
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/api/auth/login/oauth2/code")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuthService)
                        )
                        .successHandler(oAuthSuccessHandler)

                );

        http.addFilterBefore(new ExceptionHandlerFilter(objectMapper), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new TokenAuthenticationFilter(tokenAnalyzer, userRepository, tokenRepository),UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    private class CustomRedirectStrategy implements RedirectStrategy {
        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
            objectMapper.writeValue(response.getWriter(), AuthUriResponse.from(url));
        }
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        var configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
