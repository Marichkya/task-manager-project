package com.gmail.taskmanager.models;

import com.gmail.taskmanager.dto.UserNotificationsDTO;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class UserNotifications {
    @Id
    @SequenceGenerator(name = "hibernateSeq", sequenceName = "HIBERNATE_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSeqNot")
    private Long id;

    private boolean emailNotifications;
    private boolean smsNotifications;

    @OneToOne(mappedBy = "userNotifications")
    private User user;

    public UserNotifications() {
    }

    private UserNotifications(boolean emailNotifications, boolean smsNotifications) {
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
    }

    public static UserNotifications of(boolean emailNotifications, boolean smsNotifications) {
        return new UserNotifications(emailNotifications, smsNotifications);
    }

    public UserNotificationsDTO toDTO() {
        return UserNotificationsDTO.of(id, emailNotifications, smsNotifications);
    }

    public static UserNotifications fromDTO(UserNotificationsDTO userNotificationsDTO) {
        return UserNotifications.of(userNotificationsDTO.isEmailNotifications(), userNotificationsDTO.isSmsNotifications());
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserNotifications)) {
            return false;
        }
        UserNotifications that = (UserNotifications) o;
        return this.id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
