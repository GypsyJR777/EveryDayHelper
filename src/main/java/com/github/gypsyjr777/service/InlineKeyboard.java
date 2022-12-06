package com.github.gypsyjr777.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKeyboard {
    public InlineKeyboardMarkup getStartKeyboard() {
        InlineKeyboardButton goTask = new InlineKeyboardButton();
        goTask.setText("Задачи");
        goTask.setCallbackData("goTask");

        InlineKeyboardButton goWeather = new InlineKeyboardButton();
        goWeather.setText("Погода");
        goWeather.setCallbackData("goWeather");

        InlineKeyboardButton goCurrency = new InlineKeyboardButton();
        goCurrency.setText("Валюта");
        goCurrency.setCallbackData("goCurrency");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(goTask);
        keyboardButtonsRow1.add(goWeather);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(goCurrency);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getTaskKeyboard() {
        InlineKeyboardButton buttonAddTask = new InlineKeyboardButton();
        buttonAddTask.setText("Добавить задачу");
        buttonAddTask.setCallbackData("/task");

        InlineKeyboardButton buttonDeleteTask= new InlineKeyboardButton();
        buttonDeleteTask.setText("Удалить задачу");
        buttonDeleteTask.setCallbackData("/deletetask");

        InlineKeyboardButton buttonListTask = new InlineKeyboardButton();
        buttonListTask.setText("Все задачи");
        buttonListTask.setCallbackData("/tasklist");

        InlineKeyboardButton buttonDoneTask = new InlineKeyboardButton();
        buttonDoneTask.setText("Отметить задачу");
        buttonDoneTask.setCallbackData("/taskdone");

        InlineKeyboardButton buttonClearTasks = new InlineKeyboardButton();
        buttonClearTasks.setText("Очистить все");
        buttonClearTasks.setCallbackData("/clearalltasks");

        InlineKeyboardButton buttonDoneTasks = new InlineKeyboardButton();
        buttonDoneTasks.setText("Очистить сделанное");
        buttonDoneTasks.setCallbackData("/cleardone");

        InlineKeyboardButton buttonBack = new InlineKeyboardButton();
        buttonBack.setText("Назад");
        buttonBack.setCallbackData("/back");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonAddTask);
        keyboardButtonsRow1.add(buttonDeleteTask);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonListTask);
        keyboardButtonsRow2.add(buttonDoneTask);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(buttonClearTasks);
        keyboardButtonsRow3.add(buttonDoneTasks);

        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(buttonBack);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getWeatherKeyboard() {
        InlineKeyboardButton buttonWeatherToday = new InlineKeyboardButton();
        buttonWeatherToday.setText("Посмотреть погоду");
        buttonWeatherToday.setCallbackData("/weather");

        InlineKeyboardButton buttonBack = new InlineKeyboardButton();
        buttonBack.setText("Назад");
        buttonBack.setCallbackData("/back");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonWeatherToday);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonBack);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getCurrencyKeyboard() {
        InlineKeyboardButton usdRub = new InlineKeyboardButton();
        usdRub.setText("USD/RUB");
        usdRub.setCallbackData("/usdrub");

        InlineKeyboardButton eurRub = new InlineKeyboardButton();
        eurRub.setText("EUR/RUB");
        eurRub.setCallbackData("/eurrub");

        InlineKeyboardButton buttonBack = new InlineKeyboardButton();
        buttonBack.setText("Назад");
        buttonBack.setCallbackData("/back");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(usdRub);
        keyboardButtonsRow1.add(eurRub);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonBack);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
