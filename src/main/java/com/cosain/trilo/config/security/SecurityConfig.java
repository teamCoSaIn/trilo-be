package com.cosain.trilo.config.security;

import com.cosain.trilo.config.security.handler.CustomAccessDeniedHandler;
import com.cosain.trilo.config.security.handler.CustomAuthenticationEntryPoint;
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

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/deploy/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf().disable()
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
                                .baseUri("/login/oauth2/code")
                        )
                );


        return http.build();
    }

    private static class CustomRedirectStrategy implements RedirectStrategy {
        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
            response.setHeader("Auth-Url" ,url);
        }
    }


}
