package com.github.GypsyJR777.service;

import com.github.GypsyJR777.config.BotConfig;
import com.github.GypsyJR777.entity.Task;
import com.github.GypsyJR777.entity.User;
import com.github.GypsyJR777.repository.TaskRepository;
import com.github.GypsyJR777.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private boolean isTask = false;
    private final String HELP_MESSAGE = """
            Я бот-помощник в ежедневных делах
            Вот что я могу:
            /start - регистрация пользователя
            /task - добавление задачи
            /cancel - отмена последнего действия
            /tasklist - список задач
            /help - информация о командах бота
            """;

    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepository userRepository, TaskRepository taskRepository) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;

        List<BotCommand> listOfCommands = new ArrayList<>();

        listOfCommands.add(new BotCommand("/start", "Регистрация пользователя"));
        listOfCommands.add(new BotCommand("/task", "Добавление задачи"));
        listOfCommands.add(new BotCommand("/cancel", "Отмена последнего действия"));
        listOfCommands.add(new BotCommand("/tasklist", "Список задач"));
        listOfCommands.add(new BotCommand("/help", "Информация о командах бота"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start" -> {
                    String userName = update.getMessage().getChat().getUserName();
                    startCommandReceived(chatId,
                            userName == null ? update.getMessage().getChat().getFirstName() : userName,
                            update.getMessage());
                }

                case "/help" -> {
                    SendMessage message = new SendMessage();

                    message.setChatId(chatId);
                    message.setText(HELP_MESSAGE);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                case "/task" -> {
                    if (userRepository.findById(update.getMessage().getChatId()).isPresent()) {
                        SendMessage sendMessage = new SendMessage();

                        sendMessage.setChatId(update.getMessage().getChatId());
                        sendMessage.setText("Напишите Вашу задача");

                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        isTask = true;
                    } else {
                        SendMessage sendMessage = new SendMessage();

                        sendMessage.setChatId(update.getMessage().getChatId());
                        sendMessage.setText("Введите команду \"/start\" для регистрации");

                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }

                case "/cancel" -> {
                    if (!isTask) {
                        SendMessage sendMessage = new SendMessage();

                        sendMessage.setChatId(update.getMessage().getChatId());
                        sendMessage.setText("У Вас нет выполняемых действий");

                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        isTask = false;

                        SendMessage sendMessage = new SendMessage();

                        sendMessage.setChatId(update.getMessage().getChatId());
                        sendMessage.setText("Действие отменено");

                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }

                case "/tasklist" -> {
                    getTaskList(update.getMessage());
                }

                default -> {
                    if (isTask) {
                        createTask(update.getMessage());
                    }
                }
            }
        }
    }

    private void startCommandReceived(long chatId, String name, Message msg) {
        String answer = "Привет, " + name + ", рад знакомству! Если Вы хотите узнать больше о моих способностях, " +
                "то введите /help";

        createUser(msg);

        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(answer);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    private void createUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            User user = new User();

            user.setId(message.getChatId());
            user.setUserName(message.getChat().getUserName());
            user.setFirstName(message.getChat().getFirstName());
            user.setLastName(message.getChat().getLastName());
            user.setRegistered(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
        }
    }

    private void createTask(Message message) {
        SendMessage sendMessage = new SendMessage();

        if (userRepository.findById(message.getChatId()).isPresent()) {
            Task task = new Task();

            task.setTask(message.getText());
            task.setDone(false);
            task.setUser(userRepository.findById(message.getChatId()).get());

            taskRepository.save(task);

            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Задача добавлена");
        } else {
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Введите команду \"/start\" для регистрации");
        }

        isTask = false;

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getTaskList(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());

        if (userRepository.findById(message.getChatId()).isPresent()) {

            User user = userRepository.findById(message.getChatId()).get();
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

            sendMessage.setText(EmojiParser.parseToUnicode(tasks[0]));
        } else {
            sendMessage.setText("Введите команду \"/start\" для регистрации");
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
