package com.gmail.taskmanager.repositories;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import com.gmail.taskmanager.models.Task;
import com.gmail.taskmanager.models.Tasklist;
import com.gmail.taskmanager.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAuthorUsername(String username, Pageable pageable);

    List<Task> findByAuthorUsername(String username);

    Long countByAuthorUsername(String username);

    @Query("SELECT NEW com.gmail.taskmanager.dto.TaskToNotifyDTO(a.username, a.name, a.phone, t.title, t.description, t.dateFinish, t.dateToNotify, t.priority)" +
            "FROM User a, Task t WHERE (t.dateToNotify >= :from AND t.dateToNotify < :to) AND a.id = t.author")
    List<TaskToNotifyDTO> findTasksToNotify(@Param("from") Date from,
                                            @Param("to") Date to);

    @Query("SELECT t.friendsOnTask FROM Task t WHERE t.id = :id")
    List<User> findFriendsOnTasks(Long id);

    @Query("SELECT t.author FROM Task t WHERE t.id = :id")
    User findAuthorTask(Long id);

    Long countByTasklists(Tasklist tasklist);

    @Query("SELECT t FROM Task t WHERE t.author = :author AND t.priority = :priority")
    List<Task> findByAuthorAndPriority(User author, String priority, Pageable pageable);

    Long countByAuthorUsernameAndPriority(String username, String priority);

}
