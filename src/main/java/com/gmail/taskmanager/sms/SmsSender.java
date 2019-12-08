package com.gmail.taskmanager.sms;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;

public interface SmsSender {
    void sendSms(TaskToNotifyDTO task);
}
