package com.gmail.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

public class CommentDTO {
    private Long id;
    private String textComment;
    private Long taskId;
    private String authorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Europe/Kiev")
    private Date dateCreate;

    @JsonCreator
    public CommentDTO(@JsonProperty(required = true) String textComment,
                      @JsonProperty(required = true) Long taskId,
                      @JsonProperty String authorName,
                      @JsonProperty(required = true) Date dateCreate) {
        this.textComment = textComment;
        this.taskId = taskId;
        this.authorName = authorName;
        this.dateCreate = dateCreate;
    }

    private CommentDTO(Long id, String textComment, Long taskId, String authorName, Date dateCreate) {
        this.id = id;
        this.textComment = textComment;
        this.taskId = taskId;
        this.authorName = authorName;
        this.dateCreate = dateCreate;
    }

    public static CommentDTO of(Long id, String textComment, Long taskId, String authorName, Date dateCreate) {
        return new CommentDTO(id, textComment, taskId, authorName, dateCreate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommentDTO)) return false;
        CommentDTO that = (CommentDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id=" + id +
                ", textComment='" + textComment + '\'' +
                ", taskId=" + taskId +
                ", authorName='" + authorName + '\'' +
                ", dateCreate=" + dateCreate +
                '}';
    }
}
