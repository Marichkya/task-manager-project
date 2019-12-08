package com.gmail.taskmanager.services;

import com.gmail.taskmanager.dto.UserNotificationsDTO;

public interface SettingsService {

    void changePersonalData(String username, String name, String phone);

    void changeUserSecurityData(String username, String passwordHash);

    void changeUserNotifications(String username, UserNotificationsDTO userNotificationsDTO);
}
