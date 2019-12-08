package com.gmail.taskmanager.models;

import com.gmail.taskmanager.dto.TaskDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @SequenceGenerator(name = "hibernateSeq", sequenceName = "HIBERNATE_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSeqTask")
    private Long id;
    private String title;
    private String description;
    private String priority;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStart;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFinish;
    private Date dateToNotify;
    private boolean isComplete;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "task")
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinTable(name = "Users_Tasks",
            joinColumns = @JoinColumn(name = "Task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "User_id", referencedColumnName = "id"))
    private List<User> friendsOnTask = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "Tasks_Tasklists",
            joinColumns = @JoinColumn(name = "Task_id"),
            inverseJoinColumns = @JoinColumn(name = "Tasklist_id"))
    private List<Tasklist> tasklists = new ArrayList<>();

    public Task() {
    }

    private Task(String title, String description, Date dateFinish, Date dateToNotify, String priority, boolean isComplete) {
        this.title = title;
        this.description = description;
        this.dateStart = new Date();
        this.dateFinish = dateFinish;
        this.dateToNotify = dateToNotify;
        this.priority = priority;
        this.isComplete = isComplete;
    }

    public static Task of(String title, String description, Date dateFinish, Date dateToNotify, String priority, boolean isComplete) {
        return new Task(title, description, dateFinish, dateToNotify, priority, isComplete);
    }

    public TaskDTO toDTO() {
        return TaskDTO.of(id, title, description, dateStart,
                dateFinish, dateToNotify, priority, null, isComplete);
    }

    public TaskDTO toDTO(String username) {
        return TaskDTO.of(id, title, description, dateStart,
                dateFinish, dateToNotify, priority, getTaskListCurrentUser(username), isComplete);
    }

    public static Task fromDTO(TaskDTO taskDTO) {
        return Task.of(taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getDateFinish(),
                taskDTO.getDateToNotify(), taskDTO.getPriority(), false);
    }

    public void delete() {
        author.getTasks().remove(this);
        author = null;
    }

    public void addComment(Comment comment) {
        comment.setTask(this);
        author.addComment(comment);
    }

    public void addTasklist(Tasklist taskList) {
        tasklists.add(taskList);
    }

    public void deleteTasklist(Tasklist tasklist) {
        tasklists.remove(tasklist);
    }

    public void addUserToFriendsList(User user) {
        friendsOnTask.add(user);
    }

    public void deleteUserFromFriendsList(User user) {
        friendsOnTask.remove(user);
    }

    private String getTaskListCurrentUser(String username) {
        for (Tasklist tempList : tasklists) {
            if (username.equals(tempList.getAuthor().getUsername())) {
                return tempList.getName();
            }
        }
        return "Задача не добавлена в список";
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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<User> getFriendsOnTask() {
        return friendsOnTask;
    }

    public void setFriendsOnTask(List<User> friendsOnTask) {
        this.friendsOnTask = friendsOnTask;
    }

    public List<Tasklist> getTasklists() {
        return tasklists;
    }

    public void setTasklists(List<Tasklist> tasklists) {
        this.tasklists = tasklists;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return this.id.equals(task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
