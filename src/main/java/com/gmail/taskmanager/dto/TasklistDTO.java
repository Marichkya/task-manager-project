package com.gmail.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TasklistDTO {
    private Long id;
    private String name;
    private String description;
    private Integer taskCounter;

    @JsonCreator
    public TasklistDTO(@JsonProperty(required = true) String name,
                       @JsonProperty(required = true) String description) {
        this.name = name;
        this.description = description;
    }

    private TasklistDTO(Long id, String name, String description, Integer taskCounter) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskCounter = taskCounter;
    }

    public static TasklistDTO of(Long id, String name, String description, Integer taskCounter) {
        return new TasklistDTO(id, name, description, taskCounter);
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

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Integer getTaskCounter() { return taskCounter; }

    public void setTaskCounter(Integer taskCounter) { this.taskCounter = taskCounter; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TasklistDTO)) return false;
        TasklistDTO that = (TasklistDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TasklistDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskCounter=" + taskCounter +
                '}';
    }
}
