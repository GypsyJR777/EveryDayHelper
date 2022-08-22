package com.github.GypsyJR777.service;

import com.github.GypsyJR777.entity.Task;
import com.github.GypsyJR777.entity.User;
import com.github.GypsyJR777.repository.TaskRepository;
import com.github.GypsyJR777.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public String createTask(String text, Long chatId) {
        Task task = new Task();

        task.setTask(text);
        task.setDone(false);
        task.setUser(userRepository.findById(chatId).get());

        taskRepository.save(task);

        return "Задача добавлена";
    }

    public String getTaskList(Long chatId) {
        User user = userRepository.findById(chatId).get();
        final String[] tasks = {""};
        AtomicInteger count = new AtomicInteger(1);

        taskRepository.findAllByUser(user).forEach(it -> {
            if (it.isDone()) {
                tasks[0] += count + ") " + it.getTask() + "\t:heavy_check_mark:\n";
            } else {
                tasks[0] += count + ") " + it.getTask() + "\t:heavy_multiplication_x:\n";
            }

            count.getAndIncrement();
        });

        String text;

        if (tasks[0].equals("")) {
            text = "У вас нет задач";
        } else {
            text = EmojiParser.parseToUnicode(tasks[0]);
        }

        return text;
    }

    public String clearTasks(Long chatId) {
        taskRepository.deleteAll(taskRepository.findAllByUser(userRepository.findById(chatId).get()));

        return "Ваши задачи удалены";
    }

    public String taskDone(String text, Long chatId) {
        int numTask = Integer.parseInt(text) - 1;
        List<Task> tasks = taskRepository.findAllByUser(userRepository.findById(chatId).get());
        Task task = tasks.get(numTask);

        task.setDone(true);
        taskRepository.save(task);

        return "Задача отмечена";
    }

    public String clearDoneTasks(Long chatId) {
        taskRepository.deleteAll(taskRepository.findAllByUserAndDone(
                userRepository.findById(chatId).get(), true));


        return "Сделанные задачи удалены";
    }

    public String deleteTask(String text, Long chatId) {
        int numTask = Integer.parseInt(text) - 1;
        List<Task> tasks = taskRepository.findAllByUser(userRepository.findById(chatId).get());
        Task task = tasks.get(numTask);

        taskRepository.delete(task);

        return "Задача удалена";
    }
}
