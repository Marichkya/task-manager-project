package com.gmail.taskmanager.models;

import com.gmail.taskmanager.dto.CommentDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @SequenceGenerator(name = "hibernateSeq", sequenceName = "HIBERNATE_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSeqComm")
    private Long id;
    private String textComment;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    private Date dateCreate;

    public Comment() {
    }

    private Comment(@NotNull String textComment, @NotNull Date dateCreate) {
        this.textComment = textComment;
        this.dateCreate = dateCreate;
    }

    public static Comment of(@NotNull String textComment, @NotNull Date dateCreate) {
        return new Comment(textComment, dateCreate);
    }

    public CommentDTO toDTO() {
        return CommentDTO.of(id, textComment, task.getId(), author.getName(), dateCreate);
    }

    public static Comment fromDTO(CommentDTO commentDTO) {
        return Comment.of(commentDTO.getTextComment(), commentDTO.getDateCreate());
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Comment)) {
            return false;
        }
        Comment comment = (Comment) o;
        return this.id.equals(comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
