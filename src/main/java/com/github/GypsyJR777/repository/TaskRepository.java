package com.github.GypsyJR777.repository;

import com.github.GypsyJR777.entity.Task;
import com.github.GypsyJR777.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByUser(User user);

    List<Task> findAllByUserAndDone(User user, boolean done);
}
