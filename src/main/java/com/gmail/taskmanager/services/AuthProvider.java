package com.gmail.taskmanager.services;

import com.gmail.taskmanager.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AuthProvider implements AuthenticationProvider {
    @Autowired
    private GeneralService generalService;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = (User) generalService.getUserByUsername(username);

        if (user != null && (username.equals(user.getUsername()))) {

            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

            return new UsernamePasswordAuthenticationToken(user, password, authorities);
        } else {
            throw new BadCredentialsException("Username not found");
        }
    }

    public boolean supports(Class<?> arg) {
        return true;
    }
}
