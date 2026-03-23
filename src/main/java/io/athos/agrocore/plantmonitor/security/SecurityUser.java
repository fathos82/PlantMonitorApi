package io.athos.agrocore.plantmonitor.security;

import io.athos.agrocore.plantmonitor.users.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {
    private User user;

    public User getPersistentUser() {
        return user;
    }

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
//    @Override
//    public boolean isEnabled() {
//        return user.isActive();
//    }



}
