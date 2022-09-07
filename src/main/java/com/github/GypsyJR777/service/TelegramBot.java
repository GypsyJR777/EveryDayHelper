package com.github.GypsyJR777.service;

import com.github.GypsyJR777.config.BotConfig;
import com.github.GypsyJR777.entity.enums.Status;
import com.github.GypsyJR777.entity.User;
import com.github.GypsyJR777.model.currency.Currency;
import com.github.GypsyJR777.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final InlineKeyboard inlineKeyboard;
    private final WeatherService weatherService;
    private final CurrencyService currencyService;
    private final String HELP_MESSAGE = """
            Я бот-помощник в ежедневных делах
            Вот что я могу:
            /start - регистрация пользователя
            /task - добавление задачи
            /tasklist - список задач
            /taskdone - отметить сделанную задачу
            /deletetask - удалить задачу
            /clearalltasks - полная очистка задач
            /cleardone - удалить сделанные задачи
            /weather - посмотреть погоду в определенном городе
            /usdrub - посмотреть курс доллара
            /eurrub - посмотреть курс евро
            /cancel - отмена последнего действия
            /help - информация о командах бота
            Если Вам интересно посмотреть на меня изнутри, то переходите по ссылке:
            https://github.com/GypsyJR777/EveryDayHelper
            """;

    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepository userRepository, TaskService taskService,
                       InlineKeyboard inlineKeyboard, WeatherService weatherService, CurrencyService currencyService) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.inlineKeyboard = inlineKeyboard;
        this.weatherService = weatherService;
        this.currencyService = currencyService;

        List<BotCommand> listOfCommands = new ArrayList<>();

        listOfCommands.add(new BotCommand("/start", "Регистрация пользователя"));
        listOfCommands.add(new BotCommand("/task", "Добавление задачи"));
        listOfCommands.add(new BotCommand("/tasklist", "Список задач"));
        listOfCommands.add(new BotCommand("/taskdone", "Отметить сделанную задачу"));
        listOfCommands.add(new BotCommand("/deletetask", "Удалить задачу"));
        listOfCommands.add(new BotCommand("/clearalltasks", "Полная очистка задач"));
        listOfCommands.add(new BotCommand("/cleardone", "Удалить сделанные задачи"));
        listOfCommands.add(new BotCommand("/weather", "Посмотреть погоду"));
        listOfCommands.add(new BotCommand("/usdrub", "Курс доллара"));
        listOfCommands.add(new BotCommand("/eurrub", "Курс евро"));
        listOfCommands.add(new BotCommand("/cancel", "Отмена последнего действия"));
        listOfCommands.add(new BotCommand("/help", "Информация о командах бота"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();

        EditMessageText editMessageText = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            sendMessage = getByMessage(update.getMessage(), null);
        } else if (update.hasCallbackQuery()) {
            sendMessage = getByMessage(update.getCallbackQuery().getMessage(), update.getCallbackQuery());

            editMessageText = new EditMessageText();
            editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setText(sendMessage.getText());
            editMessageText.setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup());
        }

        try {
            if (editMessageText == null) {
                execute(sendMessage);
            } else {
                execute(editMessageText);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage getByMessage(Message message, CallbackQuery callbackQuery) {
        String messageText = callbackQuery != null ? callbackQuery.getData() : message.getText();
        long chatId = callbackQuery != null ? callbackQuery.getMessage().getChatId() : message.getChatId();

        User user = null;
        Status status = Status.START;

        if (checkUser(chatId)) {
            user = userRepository.findById(chatId).get();
            status = user.getPosition();
        }

        if (status == null) {
            status = Status.START;
            user.setPosition(Status.START);
            userRepository.save(user);
        }

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);

        if (user == null && !messageText.equals("/start")) {
            sendMessage.setText("Введите команду /start для регистрации");
        } else {
            System.out.println(user != null ? user.getFirstName() + " " + user.getUserName() :
                    message.getChat().getFirstName());

            switch (messageText) {
                case "/start" -> {
                    String userName = message.getChat().getUserName();
                    sendMessage.setText(
                            startCommandReceived(userName == null ? message.getChat().getFirstName() : userName,
                                    message));
                }

                case "/help" -> {
                    falseAction(user);

                    sendMessage.setText(HELP_MESSAGE);
                }

                case "/task" -> {
                    falseAction(user);

                    sendMessage.setText("Напишите Вашу задачу");

                    user.setCreatTask(true);
                    userRepository.save(user);
                }

                case "/cancel" -> {
                    if (!user.isCreatTask() && !user.isDoneTask() && !user.isDelTask() && !user.isWeather()) {
                        sendMessage.setText("У Вас нет выполняемых действий");
                    } else {
                        falseAction(user);

                        sendMessage.setText("Действие отменено");
                    }
                }

                case "/tasklist" -> {
                    falseAction(user);
                    sendMessage.setText(taskService.getTaskList(chatId));
                }

                case "/clearalltasks" -> {
                    falseAction(user);
                    sendMessage.setText(taskService.clearTasks(chatId));
                }

                case "/taskdone" -> {
                    falseAction(user);
                    sendMessage.setText("Напишите цифру сделанной задачи\n" + taskService.getTaskList(chatId));
                    user.setDoneTask(true);
                    userRepository.save(user);
                }

                case "/cleardone" -> {
                    falseAction(user);
                    sendMessage.setText(taskService.clearDoneTasks(chatId));
                }

                case "/deletetask" -> {
                    falseAction(user);
                    sendMessage.setText("Напишите цифру задачи, которую Вы хотите удалить:\n" +
                            taskService.getTaskList(chatId));

                    user.setDelTask(true);
                    userRepository.save(user);
                }

                case "/back" -> {
                    user.setPosition(Status.START);
                    status = Status.START;
                    userRepository.save(user);

                    sendMessage.setText("Главное меню:");
                }

                case "/weather" -> {
                    falseAction(user);
                    sendMessage.setText("Введите город");
                    user.setWeather(true);
                    userRepository.save(user);
                }

                case "/usdrub" -> {
                    falseAction(user);
                    Currency currency = currencyService.getCurrency("usd", "rub");
                    sendMessage.setText(currency.getError() == 200 ? "Курс доллара: " + currency.getAmount() :
                            "В данный момент сервис недоступен");
                }

                case "/eurrub" -> {
                    falseAction(user);
                    Currency currency = currencyService.getCurrency("eur", "rub");
                    sendMessage.setText(currency.getError() == 200? "Курс евро: " + currency.getAmount() :
                            "В данный момент сервис недоступен");
                }

                case "goTask" -> {
                    user.setPosition(Status.TASK);
                    status = Status.TASK;
                    falseAction(user);
                    userRepository.save(user);

                    sendMessage.setText("Задачи:");
                }
                case "goWeather" -> {
                    user.setPosition(Status.WEATHER);
                    status = Status.WEATHER;
                    userRepository.save(user);

                    sendMessage.setText("Погода:");
                }

                case "goCurrency" -> {
                    user.setPosition(Status.CURRENCY);
                    userRepository.save(user);
                    status = Status.CURRENCY;
                    sendMessage.setText("Валюта:");
                }

                default -> {
                    if (user.isCreatTask()) {
                        sendMessage.setText(taskService.createTask(messageText, chatId));
                    } else if (user.isDoneTask()) {
                        sendMessage.setText(taskService.taskDone(messageText, chatId));
                    } else if (user.isDelTask()) {
                        sendMessage.setText(taskService.deleteTask(messageText, chatId));
                    } else if (user.isWeather()) {
                        sendMessage.setText(weatherService.getWeatherByCity(messageText).toString());
                    } {
                        sendMessage.setText("Неизвестная команда, попробуйте ещё раз");
                    }
                    falseAction(user);
                }
            }
        }

        sendMessage.setReplyMarkup(getInlineKeyboard(status));

        return sendMessage;
    }

    private InlineKeyboardMarkup getInlineKeyboard(Status status) {
        switch (status) {
            case START -> {
                return inlineKeyboard.getStartKeyboard();
            }
            case TASK -> {
                return inlineKeyboard.getTaskKeyboard();
            }
            case WEATHER -> {
                return inlineKeyboard.getWeatherKeyboard();
            }
            case CURRENCY -> {
                return inlineKeyboard.getCurrencyKeyboard();
            }
        }

        return null;
    }

    private boolean checkUser(Long chatId) {
        return userRepository.findById(chatId).isPresent();
    }

    private void falseAction(User user) {
        if (user.isCreatTask() || user.isDoneTask() || user.isDelTask() || user.isWeather()) {
            user.setCreatTask(false);
            user.setDoneTask(false);
            user.setDelTask(false);
            user.setWeather(false);

            userRepository.save(user);
        }
    }

    private String startCommandReceived(String name, Message msg) {
        String answer = "Привет, " + name + ", рад знакомству! Если Вы хотите узнать больше о моих способностях, " +
                "то введите /help";

        createUser(msg);

        return answer;
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