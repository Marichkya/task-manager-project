package com.gmail.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserNotificationsDTO {
    private Long id;
    private boolean emailNotifications;
    private boolean smsNotifications;

    @JsonCreator
    public UserNotificationsDTO(@JsonProperty(required = true) boolean email, @JsonProperty(required = true) boolean sms) {
        this.emailNotifications = email;
        this.smsNotifications = sms;
    }

    private UserNotificationsDTO(Long id,
                    boolean emailNotifications,
                    boolean smsNotifications) {
        this.id = id;
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
    }

    public static UserNotificationsDTO of(Long id,
                             boolean emailNotifications,
                             boolean smsNotifications) {
        return new UserNotificationsDTO(id, emailNotifications, smsNotifications);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    @Override
    public String toString() {
        return "UserNotificationsDTO{" +
                "id=" + id +
                ", emailNotifications=" + emailNotifications +
                ", smsNotifications=" + smsNotifications +
                '}';
    }
}
