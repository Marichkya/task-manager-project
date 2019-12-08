package com.gmail.taskmanager.services;

import com.gmail.taskmanager.Exceptions.ExistsTaskAtListException;
import com.gmail.taskmanager.Exceptions.ExistsUserException;
import com.gmail.taskmanager.Exceptions.FailedActivationException;
import com.gmail.taskmanager.dto.*;
import com.gmail.taskmanager.mail.EmailSender;
import com.gmail.taskmanager.models.*;
import com.gmail.taskmanager.repositories.CommentRepository;
import com.gmail.taskmanager.repositories.TaskRepository;
import com.gmail.taskmanager.repositories.TasklistRepository;
import com.gmail.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GeneralServiceImplement implements GeneralService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TasklistRepository tasklistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSender emailSender;

    @Transactional
    @Override
    public void addUser(UserDTO userDTO) throws ExistsUserException {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ExistsUserException();
        }

        User user = User.fromDTO(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActivationCode(UUID.randomUUID().toString());

        UserNotifications userNotifications = UserNotifications.of(true, false);
        user.addNotification(userNotifications);

        userRepository.save(user);

        emailSender.sendEmailForActivation(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getAuthorTask(Long id) {
        return taskRepository.findAuthorTask(id).toDTO();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUser(String username) {
        return userRepository.findByUsername(username).toDTO();
    }

    @Transactional
    @Override
    public void activateUser(String activationCode) throws FailedActivationException {
        User user = userRepository.findByActivationCode(activationCode);

        if (user == null) {
            throw new FailedActivationException();
        }

        user.setActivationCode(null);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails getUserByUsername(String username) throws UsernameNotFoundException {

        User userFindByUsername = userRepository.findByUsername(username);
        if (userFindByUsername != null) {
            return userFindByUsername;
        }

        User userFindByGoogleUsername = userRepository.findByOtherSiteUsername(username);
        if (userFindByGoogleUsername != null) {
            return userFindByGoogleUsername;
        }

        return null;
    }

    @Transactional
    @Override
    public void addTask(String username, TaskDTO taskDTO) {
        User user = userRepository.findByUsername(username);
        Task task = Task.fromDTO(taskDTO);

        user.addTask(task);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskDTO> getTasks(String username, Pageable pageable) {
        List<TaskDTO> result = new ArrayList<>();
        List<Task> tasks = taskRepository.findByAuthorUsername(username, pageable);

        tasks.forEach((x) -> result.add(x.toDTO()));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskDTO> getTasks(String username) {
        List<Task> tasks = taskRepository.findByAuthorUsername(username);
        List<Tasklist> tasklists = tasklistRepository.findByAuthorUsername(username);

        ListIterator<Task> iterator = tasks.listIterator();
        while (iterator.hasNext()) {
            Task tempTask = iterator.next();
            for (Tasklist tempTasklist : tasklists) {
                List<Task> tempTasks = tempTasklist.getTasks();
                for (Task tempTaskTwo : tempTasks) {
                    if (tempTaskTwo.equals(tempTask)) {
                        iterator.remove();
                    }
                }
            }
        }

        List<TaskDTO> result = new ArrayList<>();
        tasks.forEach((x) -> result.add(x.toDTO()));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public TaskDTO getTask(Long id, String username) {
        return taskRepository.getOne(id).toDTO(username);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskToNotifyDTO> getTasksToNotify(Date now) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(now);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date from = calendar.getTime();

        calendar.add(Calendar.MINUTE, 1);
        Date to = calendar.getTime();

        return taskRepository.findTasksToNotify(from, to);
    }

    @Transactional
    @Override
    public void deleteTask(List<Long> idList) {
        for (int i = 0; i < idList.size(); i++) {
            Task tempTask = taskRepository.getOne(idList.get(i));

            List<Tasklist> tasklists = tempTask.getTasklists();
            for (int j = 0; j < tasklists.size(); j++) {
                Tasklist tempTasklist = tasklists.get(j);
                tempTasklist.deleteTask(tempTask);
                tempTask.deleteTasklist(tempTasklist);
            }

            List<User> taskFriends = tempTask.getFriendsOnTask();
            for (int j = 0; j < taskFriends.size(); j++) {
                User tempUser = taskFriends.get(j);
                tempUser.deleteFriendlyTask(tempTask);
                tempTask.deleteUserFromFriendsList(tempUser);
            }

            tempTask.delete();

            taskRepository.deleteById(idList.get(i));
        }
    }

    @Transactional
    @Override
    public UserNotificationsDTO getUserNotifications(String username) {
        return userRepository.findUserNotifications(username).toDTO();
    }

    @Transactional
    @Override
    public void closeTask(Long id) {
        Task task = taskRepository.getOne(id);
        task.setComplete(true);
        taskRepository.save(task);
    }

    @Transactional
    @Override
    public void changeTask(TaskDTO taskDTO) {
        Task task = taskRepository.getOne(taskDTO.getId());
        if (taskDTO.getTitle() != null) {
            task.setTitle(taskDTO.getTitle());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getDateToNotify() != null) {
            task.setDateToNotify(taskDTO.getDateToNotify());
        }
        if (taskDTO.getDateFinish() != null) {
            task.setDateFinish(taskDTO.getDateFinish());
        }
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        }
        taskRepository.save(task);
    }

    @Transactional
    @Override
    public void addComment(String username, CommentDTO commentDTO) {
        Comment comment = Comment.fromDTO(commentDTO);
        Task task = taskRepository.getOne(commentDTO.getTaskId());
        User author = userRepository.findByUsername(username);
        comment.setAuthor(author);

        task.addComment(comment);

        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> getComments(Long taskId, Pageable pageable) {
        List<CommentDTO> result = new ArrayList<>();
        List<Comment> comments = commentRepository.findByTaskId(taskId, pageable);

        comments.forEach((x) -> result.add(x.toDTO()));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<FriendDTO> getAllUsers() {
        List<FriendDTO> result = new ArrayList<>();
        List<User> allUsers = userRepository.findAll();

        allUsers.forEach((x) -> result.add(x.toFriendDTO()));
        return result;
    }

    @Transactional
    @Override
    public void addFriendOnTask(Long id, List<FriendDTO> friendsDTO) {
        Task task = taskRepository.getOne(id);
        for (int i = 0; i < friendsDTO.size(); i++) {
            User tempFriend = userRepository.findByUsername(friendsDTO.get(i).getUsername());
            task.addUserToFriendsList(tempFriend);
        }
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FriendDTO> getFriendsOnTask(Long id) {
        List<FriendDTO> result = new ArrayList<>();
        List<User> friendsOnTask = taskRepository.findFriendsOnTasks(id);

        friendsOnTask.forEach((x) -> result.add(x.toFriendDTO()));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskDTO> getTasksFriends(String username, Pageable pageable) {
        List<TaskDTO> result = new ArrayList<>();
        List<Task> tasks = userRepository.findFriendlyTasks(username, pageable);
        tasks.forEach((x) -> result.add(x.toDTO()));
        return result;
    }

    @Transactional
    @Override
    public void addTasklist(String username, TasklistDTO taskListDTO) {
        Tasklist taskList = Tasklist.fromDTO(taskListDTO);
        taskList.setTaskCounter(0);
        User user = userRepository.findByUsername(username);
        user.addTasksList(taskList);
        tasklistRepository.save(taskList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TasklistDTO> getTasklists(String username, Pageable pageable) {
        List<TasklistDTO> result = new ArrayList<>();
        List<Tasklist> tasklists = tasklistRepository.findByAuthorUsername(username, pageable);

        tasklists.forEach((x) -> result.add(x.toDTO()));
        return result;
    }

    @Override
    public List<TasklistDTO> getTasklists(String username) {
        List<TasklistDTO> result = new ArrayList<>();
        List<Tasklist> tasklists = tasklistRepository.findByAuthorUsername(username);

        tasklists.forEach((x) -> result.add(x.toDTO()));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public TasklistDTO getTasklist(Long id) {
        return tasklistRepository.getOne(id).toDTO();
    }

    @Transactional
    @Override
    public void addTaskToTasklist(String username, Long taskId, Long listId) throws ExistsTaskAtListException {
        List<Tasklist> tasklists = tasklistRepository.findByAuthorUsername(username);
        Task task = taskRepository.getOne(taskId);
        for (Tasklist tempList : tasklists) {
            for (Task tempTask : tempList.getTasks()) {
                if (task.equals(tempTask)) {
                    throw new ExistsTaskAtListException();
                }
            }
        }
        Tasklist tasklist = tasklistRepository.getOne(listId);
        tasklist.addTask(task);
        tasklistRepository.save(tasklist);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskDTO> getTasksForTasklist(Long id, Pageable pageable) {
        List<TaskDTO> result = new ArrayList<>();
        List<Task> tasksLists = tasklistRepository.findTaskForTasksList(id, pageable);

        tasksLists.forEach((x) -> result.add(x.toDTO()));
        return result;
    }

    @Transactional
    @Override
    public void deleteTasklist(Long id) {
        tasklistRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void changeTasklist(TasklistDTO tasklistDTO) {
        Tasklist tasklist = tasklistRepository.getOne(tasklistDTO.getId());
        if (tasklistDTO.getName() != null) {
            tasklist.setName(tasklistDTO.getName());
        }
        if (tasklistDTO.getDescription() != null) {
            tasklist.setDescription(tasklistDTO.getDescription());
        }
        tasklistRepository.save(tasklist);
    }

    @Transactional
    @Override
    public void deleteFriend(Long taskId, Long friendId) {
        Task task = taskRepository.getOne(taskId);
        User user = userRepository.getOne(friendId);

        task.deleteUserFromFriendsList(user);
        user.deleteFriendlyTask(task);

        taskRepository.save(task);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteTaskFromTasklist(Long listId, List<Long> tasksId) {
        Tasklist tasklist = tasklistRepository.getOne(listId);
        List<Task> tasks = tasklist.getTasks();

        ListIterator<Task> iterator = tasks.listIterator();
        for (Long tempId : tasksId) {
            while (iterator.hasNext()) {
                Task tempTask = iterator.next();
                if (tempTask.getId().equals(tempId)) {
                    iterator.remove();
                    tasklist.setTaskCounter(tasklist.getTaskCounter() - 1);
                    break;
                }
            }
        }

        tasklist.setTasks(tasks);
        tasklistRepository.save(tasklist);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskDTO> getUserTasksHighPriority(String username, Pageable pageable) {
        User author = userRepository.findByUsername(username);
        List<TaskDTO> result = new ArrayList<>();
        List<Task> tasksLists = taskRepository.findByAuthorAndPriority(author, "Високий", pageable);

        tasksLists.forEach((x) -> result.add(x.toDTO()));
        return result;
    }
}
