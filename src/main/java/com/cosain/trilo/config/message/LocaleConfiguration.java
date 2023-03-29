package com.cosain.trilo.config.message;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class LocaleConfiguration implements WebMvcConfigurer {

    /**
     * LocaleResolver : 클라이언트의 언어&국가 정보를 인식
     * - AcceptHeaderLocaleResolver : 사용자의 Accept-Language 헤더를 이용하여 언어&국가 정보를 인식
     */
    @Bean
    public LocaleResolver localeResolver() {
        var resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.KOREAN); // 언어 & 국가 정보가 없는 경우 한국으로 인식하도록 설정
        return resolver;
    }

    /**
     * 요청을 가로채서, 쿼리 파라미터에 lang 파라미터가 전달됐을 경우 해당 값을 클라이언트의 언어 & 국가 정보로 인식하도록 한다.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

}
