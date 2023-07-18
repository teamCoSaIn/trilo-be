package com.cosain.trilo.config;

import com.cosain.trilo.auth.presentation.AuthArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final List<HandlerInterceptor> interceptors;
    private final AuthArgumentResolver authArgumentResolver;

    private static final String ALLOWED_HTTP_METHODS = "GET,POST,HEAD,PUT,PATCH,DELETE,OPTIONS";
    private static final String FE_LOCALHOST = "http://localhost:3000";
    private static final String DOMAIN = "http://cosain-trilo.com";
    private static final String WWW_DOMAIN = "http://www.cosain-trilo.com";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        interceptors.forEach(registry::addInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods(ALLOWED_HTTP_METHODS.split(","))
                .allowedOrigins(DOMAIN, WWW_DOMAIN, FE_LOCALHOST)
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.LOCATION);
    }
}
