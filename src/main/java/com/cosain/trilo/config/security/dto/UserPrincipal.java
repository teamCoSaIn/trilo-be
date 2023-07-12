package com.cosain.trilo.config.security.dto;

import com.cosain.trilo.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
@Getter
public class UserPrincipal implements OAuth2User {

    private User user;
    private Map<String, Object> attributes;

    private UserPrincipal(User user){
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(() -> user.getRole().toString());

        return collect;
    }

    @Override
    public String getName() {
        return user.getNickName();
    }

    public static UserPrincipal from(User user){
        return new UserPrincipal(user);
    }

    public static UserPrincipal of(User user, Map<String, Object> attributes){
        UserPrincipal userPrincipal = UserPrincipal.from(user);
        userPrincipal.attributes = attributes;
        return userPrincipal;
    }


    public Long getId() {
        return user.getId();
    }
}
