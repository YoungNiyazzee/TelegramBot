package com.example.telegrambot;

import com.jayway.jsonpath.JsonPath;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${telegram.token}")
    String telegramToken;

    @Value("${telegram.username}")
    String telegramUsername;

    public ReplyKeyboard getKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow firstKeyboardRow = new KeyboardRow();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        firstKeyboardRow.add(new KeyboardButton("Boxer"));
        firstKeyboardRow.add(new KeyboardButton("Doberman"));
        firstKeyboardRow.add(new KeyboardButton("Husky"));
        keyboard.add(firstKeyboardRow);
        return replyKeyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return telegramUsername;
    }

    @Override
    public String getBotToken() {
        return telegramToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            String message_text = update.getMessage().getText();
            String chat_id = String.valueOf(update.getMessage().getChatId());
            switch (message_text) {
                case "/start":
                    message.setText("Привет! Я чат-бот. И я могу показывать картинки милых собачек. Для выбора породы собак, нажмите соответствующую кнопку на панели.");
                    message.setChatId(chat_id);
                    message.setReplyMarkup(getKeyboard());
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Boxer":
                    message.setText(getTheBoxerPhoto());
                    message.setChatId(chat_id);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Doberman":
                    message.setText(getTheDobermanPhoto());
                    message.setChatId(chat_id);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Husky":
                    message.setText(getTheHuskyPhoto());
                    message.setChatId(chat_id);
                    try {
                        execute(message);
                    }catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public String getTheBoxerPhoto(){
        Request request = new Request.Builder()
                .url("https://dog.ceo/api/breed/boxer/images/random")
                .build();
        return getPhotoUrl(request);
    }

    public String getTheDobermanPhoto() {
        Request request = new Request.Builder()
                .url("https://dog.ceo/api/breed/doberman/images/random")
                .build();
        return getPhotoUrl(request);
    }

    public String getTheHuskyPhoto() {
        Request request = new Request.Builder()
                .url("https://dog.ceo/api/breed/husky/images/random")
                .build();
        return getPhotoUrl(request);
    }

    private String getPhotoUrl(Request request) {
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String jsonResponse = response.body().string();
            return JsonPath.read(jsonResponse, "$.message");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Connection error";
    }
}