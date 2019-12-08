package com.gmail.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.taskmanager.models.Role;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String phone;
    private String otherSiteName;
    private String otherSiteUsername;
    private boolean active;
    private String activationCode;

    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @JsonCreator
    public UserDTO(@JsonProperty(required = true) String name,
                   @JsonProperty(required = true) String username,
                   @JsonProperty(required = true) String password,
                   @JsonProperty String phone) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.active = true;
        this.roles = Collections.singleton(Role.USER);
    }

    private UserDTO(Long id,
                    String name,
                    String username,
                    String password,
                    String phone,
                    String otherSiteName,
                    String otherSiteUsername,
                    boolean active,
                    String activationCode,
                    Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.otherSiteName = otherSiteName;
        this.otherSiteUsername = otherSiteUsername;
        this.active = active;
        this.activationCode = activationCode;
        this.roles = roles;
    }

    public static UserDTO of(Long id,
                             String name,
                             String username,
                             String password,
                             String phone,
                             String otherSiteName,
                             String otherSiteUsername,
                             boolean active,
                             String activationCode,
                             Set<Role> roles) {
        return new UserDTO(id, name, username, password, phone, otherSiteName, otherSiteUsername,
                active, activationCode, roles);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationCode() { return activationCode; }

    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }

    public String getOtherSiteName() {
        return otherSiteName;
    }

    public void setOtherSiteName(String otherSiteName) {
        this.otherSiteName = otherSiteName;
    }

    public String getOtherSiteUsername() {
        return otherSiteUsername;
    }

    public void setOtherSiteUsername(String otherSiteUsername) {
        this.otherSiteUsername = otherSiteUsername;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserDTO)) return false;
        UserDTO userDTO = (UserDTO) o;
        return username.equals(userDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", otherSiteName='" + otherSiteName + '\'' +
                ", otherSiteUsername='" + otherSiteUsername + '\'' +
                ", active=" + active +
                ", roles=" + roles +
                '}';
    }
}
