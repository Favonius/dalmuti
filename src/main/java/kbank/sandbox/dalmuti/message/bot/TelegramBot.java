package kbank.sandbox.dalmuti.message.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * <pre>
 * 파 일 명 : TelegramBot.java
 * 설    명 : Telegram Bot
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String BOT_NAME = "dalmutik";

    private final String AUTH_KEY = "1480423142:AAHkkAlgShepdoXFW2HP8TzZAiRfCN8WpHI";

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return AUTH_KEY;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    /**
     * 텔레그램 메시지를 전송한다.
     *
     * @param : chatId 채팅 ID, sendMessage 메시지 정보
     * @return :
     */
    public void sendMessage(String chatId, String sendMessage) {
        SendMessage message = new SendMessage().setChatId(chatId).setText(sendMessage).setParseMode("MarkDown");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
