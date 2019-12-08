package com.gmail.taskmanager.sms;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsSender implements SmsSender {
    @Value("${twilio.accountSid}")
    private String accountSid;
    @Value("${twilio.authToken}")
    private String authToken;
    @Value("${twilio.senderNumber}")
    private String senderNumber;

    @Autowired
    private SmsMessage smsMessage;

    public void sendSms(TaskToNotifyDTO task) {
        Twilio.init(accountSid, authToken);

        String textMessage = smsMessage.toFormMessage(task);

        Message message = Message
                .creator(new PhoneNumber(task.getPhone()), // to
                        new PhoneNumber(senderNumber), // from
                        textMessage)
                .create();

        System.out.println(message.getSid());
    }

}
