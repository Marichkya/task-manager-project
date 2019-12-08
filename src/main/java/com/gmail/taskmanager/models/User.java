package com.gmail.taskmanager.models;

import com.gmail.taskmanager.dto.FriendDTO;
import com.gmail.taskmanager.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "hibernateSeq", sequenceName = "HIBERNATE_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSeqUser")
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String username;

    private String password;
    private String phone;
    private String otherSiteName;
    private String otherSiteUsername;
    private boolean active;
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "author")
    private List<Task> tasks = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_notifications_id")
    private UserNotifications userNotifications;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "Users_Tasks",
            joinColumns = @JoinColumn(name = "User_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "Task_id", referencedColumnName = "id"))
    private List<Task> friendlyTasks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    private List<Tasklist> tasklists = new ArrayList<>();

    public User() {
    }

    private User(@NotNull String name,
                 @NotNull String username,
                 String password,
                 String phone,
                 String otherSiteName,
                 String otherSiteUsername,
                 boolean active,
                 Set<Role> roles) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.otherSiteName = otherSiteName;
        this.otherSiteUsername = otherSiteUsername;
        this.active = active;
        this.roles = roles;
    }

    public static User of(@NotNull String name,
                          @NotNull String username,
                          String password,
                          String phone,
                          String otherSiteName,
                          String otherSiteUsername,
                          boolean active,
                          Set<Role> roles) {
        return new User(name, username, password, phone, otherSiteName, otherSiteUsername,
                active, roles);
    }

    public void addTask(Task task) {
        task.setAuthor(this);
        tasks.add(task);
    }

    public void addNotification(UserNotifications userNotifications) {
        userNotifications.setUser(this);
        this.userNotifications = userNotifications;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addTasksList(Tasklist taskList) {
        taskList.setAuthor(this);
        this.tasklists.add(taskList);
    }

    public void addFriendlyTask(Task friendlyTask) {
        friendlyTasks.add(friendlyTask);
    }

    public void deleteFriendlyTask(Task task) {
        friendlyTasks.remove(task);
    }

    public UserDTO toDTO() {
        return UserDTO.of(id, name, username, password, phone, otherSiteName, otherSiteUsername, active, activationCode, roles);
    }

    public FriendDTO toFriendDTO() {
        return new FriendDTO(id, name, username);
    }

    public static User fromDTO(UserDTO userDTO) {
        return User.of(
                userDTO.getName(),
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getPhone(),
                userDTO.getOtherSiteName(),
                userDTO.getOtherSiteUsername(),
                userDTO.isActive(),
                userDTO.getRoles());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getOtherSiteName() {
        return otherSiteName;
    }

    public void setOtherSiteName(String otherSiteName) {
        this.otherSiteName = otherSiteName;
    }

    public String getOtherSiteUsername() {
        return otherSiteUsername;
    }

    public void setOtherSiteUsername(String otherSiteUsername) {
        this.otherSiteUsername = otherSiteUsername;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public UserNotifications getUserNotifications() {
        return userNotifications;
    }

    public void setUserNotifications(UserNotifications userNotifications) {
        this.userNotifications = userNotifications;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Task> getFriendlyTasks() {
        return friendlyTasks;
    }

    public void setFriendlyTasks(List<Task> friendlyTasks) {
        this.friendlyTasks = friendlyTasks;
    }

    public List<Tasklist> getTasklists() {
        return tasklists;
    }

    public void setTasklists(List<Tasklist> tasklists) {
        this.tasklists = tasklists;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return this.username.equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
