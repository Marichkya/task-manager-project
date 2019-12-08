package com.gmail.taskmanager.mail;

import com.gmail.taskmanager.dto.FriendDTO;
import com.gmail.taskmanager.dto.TaskDTO;
import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import com.gmail.taskmanager.dto.UserDTO;
import com.gmail.taskmanager.models.User;

import java.util.List;

public interface EmailSender {
    void sendEmailForActivation(User user);
    void sendEmailNotification(TaskToNotifyDTO task);
    void sendEmailAboutComment(UserDTO authorTask, TaskDTO task);
    void sendEmailAboutComment(UserDTO authorTask, List<FriendDTO> friends, TaskDTO task);
    void sendEmailAboutSharedTask(UserDTO authorTask, List<FriendDTO> friends);
}
