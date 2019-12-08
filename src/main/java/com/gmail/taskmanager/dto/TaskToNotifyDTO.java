package com.gmail.taskmanager.dto;

import java.util.Date;

public class TaskToNotifyDTO {
    private final String username;
    private final String name;
    private final String phone;
    private final String title;
    private final String description;
    private final Date dateFinish;
    private final Date dateToNotify;
    private final String priority;

    public TaskToNotifyDTO(String username, String name, String phone, String title,
                           String description, Date dateFinish, Date dateToNotify, String priority) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.title = title;
        this.description = description;
        this.dateFinish = dateFinish;
        this.dateToNotify = dateToNotify;
        this.priority = priority;
    }

    public String getUsername() {
        return username;
    }

    public String getName() { return name; }

    public String getTitle() { return title; }

    public String getDescription() {
        return description;
    }

    public Date getDateFinish() {
        return dateFinish;
    }

    public Date getDateToNotify() {
        return dateToNotify;
    }

    public String getPhone() { return phone; }

    public String getPriority() { return priority; }

    @Override
    public String toString() {
        return "TaskToNotifyDTO{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", description='" + description + '\'' +
                ", dateFinish=" + dateFinish +
                ", dateToNotify=" + dateToNotify +
                '}';
    }
}
