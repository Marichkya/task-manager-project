package com.gmail.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private Date dateStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Europe/Kiev")
    private Date dateFinish;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Europe/Kiev")
    private Date dateToNotify;
    private String listName;
    private boolean isComplete;


    @JsonCreator
    public TaskDTO(@JsonProperty Long id,
                   @JsonProperty(required = true) String title,
                   @JsonProperty(required = true) String description,
                   @JsonProperty(required = true) Date dateFinish,
                   @JsonProperty(required = true) Date dateToNotify,
                   @JsonProperty(required = true) String priority) {
        if (id != null) {
            this.id = id;
        }
        this.title = title;
        this.description = description;
        this.dateStart = new Date();
        this.dateFinish = dateFinish;
        this.dateToNotify = dateToNotify;
        this.priority = priority;
    }

    private TaskDTO(Long id, String title, String description, Date dateStart, Date dateFinish,
                    Date dateToNotify, String priority, String listName, boolean isComplete) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateStart = dateStart;
        this.dateFinish = dateFinish;
        this.dateToNotify = dateToNotify;
        this.priority = priority;
        this.listName = listName;
        this.isComplete = isComplete;
    }

    public static TaskDTO of(Long id, String title, String description, Date dateStart, Date dateFinish,
                             Date dateToNotify, String priority, String listName, boolean isComplete) {
        return new TaskDTO(id, title, description, dateStart, dateFinish, dateToNotify, priority, listName, isComplete);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public Date getDateToNotify() {
        return dateToNotify;
    }

    public void setDateToNotify(Date dateToNotify) {
        this.dateToNotify = dateToNotify;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TaskDTO)) return false;
        TaskDTO taskDTO = (TaskDTO) o;
        return id.equals(taskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", dateStart=" + dateStart +
                ", dateFinish=" + dateFinish +
                ", dateToNotify=" + dateToNotify +
                ", listName='" + listName + '\'' +
                ", isComplete=" + isComplete +
                '}';
    }
}
