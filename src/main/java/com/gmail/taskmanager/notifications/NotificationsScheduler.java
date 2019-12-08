package com.gmail.taskmanager.notifications;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import com.gmail.taskmanager.dto.UserNotificationsDTO;
import com.gmail.taskmanager.mail.EmailSender;
import com.gmail.taskmanager.services.GeneralService;
import com.gmail.taskmanager.sms.SmsSender;
import com.gmail.taskmanager.sms.SmscSmsSender;
import com.gmail.taskmanager.sms.TextlocalSmsSender;
import com.gmail.taskmanager.sms.TwilioSmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Component
public class NotificationsScheduler {
    @Value("${sms.sender.provider}")
    private String senderProvider;

    @Autowired
    private EmailSender emailSender;

    private SmsSender smsSender;

    @Autowired
    private GeneralService generalService;

    @Autowired
    private ApplicationContext applicationContext;

    @Async
    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    public void sendNotification() {
        List<TaskToNotifyDTO> tasks = generalService.getTasksToNotify(new Date());
        for (TaskToNotifyDTO task : tasks) {
            UserNotificationsDTO userNotifications = generalService.getUserNotifications(task.getUsername());
            boolean email = userNotifications.isEmailNotifications();
            boolean sms = userNotifications.isSmsNotifications();

            if (!email && !sms) {
                continue;
            }
            if (email) {
                emailSender.sendEmailNotification(task);
            }
            if (task.getPhone() != null && !task.getPhone().equals("") && sms) {
                smsSender.sendSms(task);
            }
        }
    }

    @PostConstruct
    private void setSmsSender() {
        if ("twilioSmsSender".equals(senderProvider)) {
            this.smsSender = applicationContext.getBean(TwilioSmsSender.class);
            return;
        }
        if ("smscSmsSender".equals(senderProvider)) {
            this.smsSender = applicationContext.getBean(SmscSmsSender.class);
            return;
        }
        if ("textlocalSmsSender".equals(senderProvider)) {
            this.smsSender = applicationContext.getBean(TextlocalSmsSender.class);
        }
    }

}
