package com.gmail.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class FriendDTO {
    private Long id;
    private String name;
    private String username;

    @JsonCreator
    public FriendDTO(@JsonProperty(required = true) String name,
                     @JsonProperty(required = true) String username) {
        this.name = name;
        this.username = username;
    }

    public FriendDTO(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FriendDTO)) return false;
        FriendDTO friendDTO = (FriendDTO) o;
        return username.equals(friendDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "FriendDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
