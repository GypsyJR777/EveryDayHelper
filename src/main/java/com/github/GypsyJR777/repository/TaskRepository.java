package com.github.GypsyJR777.repository;

import com.github.GypsyJR777.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {

}
