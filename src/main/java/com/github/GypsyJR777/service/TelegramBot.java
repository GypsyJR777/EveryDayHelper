package com.github.GypsyJR777.service;

import com.github.GypsyJR777.config.BotConfig;
import com.github.GypsyJR777.entity.User;
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

    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepository userRepository) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
//        listofCommands.add(new BotCommand("/mydata", "get your data stored"));
//        listofCommands.add(new BotCommand("/deletedata", "delete my data"));
//        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
//        listofCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                String userName = update.getMessage().getChat().getUserName();
                startCommandReceived(chatId, userName == null ? update.getMessage().getChat().getFirstName() : userName,
                        update.getMessage());
            }

//            SendMessage message = new SendMessage();
//
//            message.setChatId(chat_id);
//            message.setText(messageText);
//
//            try {
//                execute(message);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
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
}
