package com.gmail.taskmanager.repositories;

import com.gmail.taskmanager.models.Task;
import com.gmail.taskmanager.models.User;
import com.gmail.taskmanager.models.UserNotifications;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false " +
            "END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    User findByUsername(String username);

    User findByOtherSiteUsername(String otherSiteUsername);

    @Query("SELECT u.friendlyTasks FROM User u WHERE u.username = :username")
    List<Task> findFriendlyTasks(String username, Pageable pageable);

    @Query("SELECT SIZE(u.friendlyTasks) FROM User u WHERE u.username = :username")
    Long sizeFriendlyTasks(String username);

    @Query("SELECT u.userNotifications FROM User u WHERE u.username = :username")
    UserNotifications findUserNotifications(String username);

    @Query("SELECT u FROM User u WHERE u.activationCode = :activationCode")
    User findByActivationCode(String activationCode);
}
