package com.github.GypsyJR777.service;

import com.github.GypsyJR777.config.BotConfig;
import com.github.GypsyJR777.entity.Task;
import com.github.GypsyJR777.entity.User;
import com.github.GypsyJR777.repository.TaskRepository;
import com.github.GypsyJR777.repository.UserRepository;
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

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private boolean isTask = false;

    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepository userRepository, TaskRepository taskRepository) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Приветственно сообщение"));
        listOfCommands.add(new BotCommand("/task", "Сосздайте себе цель)"));
        listOfCommands.add(new BotCommand("/cancel", "Отмена последнего действия"));
//        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
//        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/help", "Информация о командах босса"));
//        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (isTask) {
            createTask(update.getMessage());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
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
                    message.setText("Здесь будет помощь) (когда-нибудь)");

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                case "/task" -> {
                    if (userRepository.findById(update.getMessage().getChatId()).isPresent()) {
                        createTask(update.getMessage());
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
            }
        }
    }

    private void startCommandReceived(long chatId, String name, Message msg) {
        String answer = "Привет, " + name + ", рад знакомству!";

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
        if (userRepository.findById(message.getChatId()).isPresent()) {
            Task task = new Task();

            task.setTask(message.getText());
            task.setDone(false);
            task.setUser(userRepository.findById(message.getChatId()).get());

            taskRepository.save(task);
        } else {
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Введите команду \"/start\" для регистрации");

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        isTask = false;
    }
}
