package kbank.sandbox.dalmuti.message.business;

import kbank.sandbox.dalmuti.game.business.GameManager;
import kbank.sandbox.dalmuti.message.bot.TelegramBot;
import kbank.sandbox.dalmuti.message.dto.MessageForm;
import kbank.sandbox.dalmuti.user.business.UserManager;
import kbank.sandbox.dalmuti.user.dto.UserForm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * <pre>
 * 파 일 명 : MessageManager.java
 * 설    명 : 메시지 처리 관리
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class MessageManager {

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    private final UserManager userManager;

    private final TelegramBot telegramBot;

    /**
     * 텔레그램 메시지를 전송한다.
     *
     * @param : sendMessage 메시지 정보
     * @return :
     * 1. 사용자 검증
     */
    @Transactional
    public void sendMessage(MessageForm messageForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("sendMessage info: {}", messageForm.toString());
        }
        // 1. 사용자 검증
        UserForm userForm = userManager.getUser(UserForm.convert(messageForm.getUserId()));

        String message = "게임코드:[" + messageForm.getGameId() + "](" + messageForm.getMessage() + ")(" + userForm.getUserName() + ")";

        telegramBot.sendMessage(messageForm.getChatId(), message);

    }
}
