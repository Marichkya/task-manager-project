package com.gmail.taskmanager.models;

import com.gmail.taskmanager.dto.TasklistDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tasklists")
public class Tasklist {
    @Id
    @SequenceGenerator(name = "hibernateSeq", sequenceName = "HIBERNATE_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSeqTsL")
    private Long id;

    private String name;

    private String description;

    private Integer taskCounter;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "Tasks_Tasklists",
            joinColumns = @JoinColumn(name = "Tasklist_id"),
            inverseJoinColumns = @JoinColumn(name = "Task_id"))
    private List<Task> tasks = new ArrayList<>();

    public Tasklist() {
    }

    private Tasklist(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public static Tasklist of(@NotNull String name, @NotNull String description) {
        return new Tasklist(name, description);
    }

    public void addTask(Task task) {
        tasks.add(task);
        taskCounter++;
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
        if (taskCounter>=1) {
            taskCounter--;
        }
    }

    public TasklistDTO toDTO() {
        return TasklistDTO.of(id, name, description, taskCounter);
    }

    public static Tasklist fromDTO(TasklistDTO taskListDTO) {
        return Tasklist.of(taskListDTO.getName(), taskListDTO.getDescription());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Integer getTaskCounter() { return taskCounter; }

    public void setTaskCounter(Integer taskCounter) { this.taskCounter = taskCounter; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tasklist)) {
            return false;
        }
        Tasklist tasklist = (Tasklist) o;
        return id.equals(tasklist.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
