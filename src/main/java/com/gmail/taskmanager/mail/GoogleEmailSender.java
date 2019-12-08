package com.gmail.taskmanager.mail;

import com.gmail.taskmanager.dto.FriendDTO;
import com.gmail.taskmanager.dto.TaskDTO;
import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import com.gmail.taskmanager.dto.UserDTO;
import com.gmail.taskmanager.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleEmailSender implements EmailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    @Value("${host.redirect.url}")
    private String url;

    @Override
    public void sendEmailForActivation(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailSender);
        mailMessage.setTo(user.getUsername());
        mailMessage.setSubject("Дякуємо за реєстрацію!");
        mailMessage.setText(
                String.format("%s, дякуємо за реєстрацію в Task Manager! " + '\n' + '\n' +
                        "Для того, щоб закінчити реєстрацію, будь ласка, перейдіть за посиланням: %suser/activate?code=%s" + '\n' + '\n' + '\n' +
                        "Дякуємо," + '\n' + " Task Manager", user.getName(), url, user.getActivationCode()
                ));

        mailSender.send(mailMessage);
    }

    @Override
    public void sendEmailNotification(TaskToNotifyDTO task) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailSender);
        mailMessage.setTo(task.getUsername());
        mailMessage.setSubject("Нагадуємо Вам про важливу справу!");
        mailMessage.setText(
                String.format("Шавовний користувачу %s!" + '\n' +
                                "Хочемо Вам нагадати про важливі плани:" + '\n' +
                                "Тема: %s" + '\n' +
                                "Пріоритет: %s" + '\n' +
                                "Час закінчення: %s" + '\n' +
                                "Опис: %s" + '\n' + '\n' + '\n' +
                                "Task Manager",
                        task.getName(), task.getTitle(), task.getPriority(), task.getDateFinish(), task.getDescription()
                ));

        mailSender.send(mailMessage);
    }

    @Override
    public void sendEmailAboutComment(UserDTO authorTask, TaskDTO task) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailSender);
        mailMessage.setTo(authorTask.getUsername());
        mailMessage.setSubject("Новий коментар");
        mailMessage.setText(
                String.format(
                        "Шановний користувач %s!" + '\n' +
                                "Хтось з ваших друзів добавив коментар до вашої задачі: %s." + '\n' + '\n' + '\n' +
                                "Task Manager",
                        authorTask.getName(), task.getTitle()
                ));

        mailSender.send(mailMessage);
    }

    @Override
    public void sendEmailAboutSharedTask(UserDTO authorTask, List<FriendDTO> friends) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailSender);

        for (FriendDTO tempFriend : friends) {
            mailMessage.setTo(tempFriend.getUsername());
            mailMessage.setSubject("Нова задача");
            mailMessage.setText(
                    String.format(
                            "Шановний користувач %s!" + '\n' +
                                    "%s користувач поділився з вами своєю задачею" + '\n' + '\n' + '\n' +
                                    "Task Manager",
                            tempFriend.getName(), authorTask.getName()
                    ));
            mailSender.send(mailMessage);
        }
    }

    @Override
    public void sendEmailAboutComment(UserDTO authorTask, List<FriendDTO> friends, TaskDTO task) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailSender);

        for (FriendDTO tempFriend : friends) {
            mailMessage.setTo(tempFriend.getUsername());
            mailMessage.setSubject("Новий коментар");
            mailMessage.setText(
                    String.format(
                            "Шановний користувач %s!" + '\n' +
                                    "До задачі: %s" + '\n' +
                                    "Автор: %s" + '\n' +
                                    "Добавили новий коментар" + '\n' + '\n' + '\n' +
                                    "Task Manager",
                            tempFriend.getName(), task.getTitle(), authorTask.getName()
                    ));
            mailSender.send(mailMessage);
        }
    }
}
