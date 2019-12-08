package com.gmail.taskmanager.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority
{
    NOT_ACTIVE_USER,
    USER;

    @Override
    public String getAuthority()
    {
        return name();
    }
}