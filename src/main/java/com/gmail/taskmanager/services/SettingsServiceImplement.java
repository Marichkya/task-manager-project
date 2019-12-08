package com.gmail.taskmanager.services;

import com.gmail.taskmanager.dto.UserNotificationsDTO;
import com.gmail.taskmanager.models.User;
import com.gmail.taskmanager.models.UserNotifications;
import com.gmail.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsServiceImplement implements SettingsService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void changePersonalData(String username, String name, String phone) {
        User user = userRepository.findByUsername(username);
        if (name != null) {
            user.setName(name);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (user.getOtherSiteUsername() != null) {
            user.setOtherSiteName(name);
        }

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void changeUserSecurityData(String username, String passwordHash) {
        User user = userRepository.findByUsername(username);
        user.setName(passwordHash);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void changeUserNotifications(String username, UserNotificationsDTO userNotificationsDTO) {
        User user = userRepository.findByUsername(username);
        UserNotifications userNotifications = user.getUserNotifications();

        userNotifications.setEmailNotifications(userNotificationsDTO.isEmailNotifications());
        userNotifications.setSmsNotifications(userNotificationsDTO.isSmsNotifications());

        user.addNotification(userNotifications);

        userRepository.save(user);
    }
}
