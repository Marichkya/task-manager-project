package com.gmail.taskmanager.sms;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsMessage {

    @Autowired
    private Transcriptor transcriptor;

    public String toFormMessage(TaskToNotifyDTO task) {
        String smsMessage = "Shanovnyi " + transcriptor.getTranslit(task.getName()) + "!" + '\n' +
                "Ne zabudte: " + transcriptor.getTranslit(task.getDescription()) + '\n' +
                "Chas zaversheniya:" + task.getDateFinish() + '\n' +
                "Task Manager";
        return smsMessage;
    }
}
