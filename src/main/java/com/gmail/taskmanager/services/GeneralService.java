package com.gmail.taskmanager.services;

import com.gmail.taskmanager.Exceptions.ExistsTaskAtListException;
import com.gmail.taskmanager.Exceptions.ExistsUserException;
import com.gmail.taskmanager.Exceptions.FailedActivationException;
import com.gmail.taskmanager.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

public interface GeneralService {

    void addUser(UserDTO userDTO) throws ExistsUserException;

    void activateUser(String activationCode) throws FailedActivationException;

    UserDTO getUser(String username);

    UserDetails getUserByUsername(String username);

    UserNotificationsDTO getUserNotifications(String username);

    void addTask(String username, TaskDTO taskDTO);

    TaskDTO getTask(Long id, String username);

    List<TaskDTO> getTasks(String username, Pageable pageable);

    List<TaskDTO> getTasks(String username);

    UserDTO getAuthorTask(Long id);

    List<TaskToNotifyDTO> getTasksToNotify(Date now);

    void deleteTask(List<Long> idList);

    void closeTask(Long id);

    void changeTask(TaskDTO taskDTO);

    void addComment(String username, CommentDTO commentDTO);

    List<CommentDTO> getComments(Long taskId, Pageable pageable);

    List<FriendDTO> getAllUsers();

    void addFriendOnTask(Long id, List<FriendDTO> friendsDTO);

    List<FriendDTO> getFriendsOnTask(Long id);

    List<TaskDTO> getTasksFriends(String username, Pageable pageable);

    void addTasklist(String username, TasklistDTO taskListDTO);

    TasklistDTO getTasklist(Long id);

    List<TasklistDTO> getTasklists(String username, Pageable pageable);

    List<TasklistDTO> getTasklists(String username);

    void addTaskToTasklist(String username, Long taskId, Long listId) throws ExistsTaskAtListException;

    List<TaskDTO> getTasksForTasklist(Long id, Pageable pageable);

    void deleteTasklist(Long id);

    void changeTasklist(TasklistDTO tasklistDTO);

    void deleteFriend(Long taskId, Long friendId);

    void deleteTaskFromTasklist(Long listId, List<Long> tasksId);

    List<TaskDTO> getUserTasksHighPriority(String username, Pageable pageable);
}
