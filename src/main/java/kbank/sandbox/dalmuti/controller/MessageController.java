package kbank.sandbox.dalmuti.controller;

import kbank.sandbox.dalmuti.message.dto.MessageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

/**
 * <pre>
 * 파 일 명 : MessageController.java
 * 설    명 : 메세징 컨트롤러
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by KBANK. All rights reserved.
 * </pre>
 */
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations messagingTemplates;

    @MessageMapping("/game")
    public void gameMessage(MessageForm messageForm) {
        messagingTemplates.convertAndSend("/topic/game/" + messageForm.getGameId(), messageForm);
    }

    @MessageMapping("/chat")
    public void chatMessage(MessageForm messageForm) {
        messagingTemplates.convertAndSend("/topic/chat/" + messageForm.getGameId(), messageForm);
    }
}
