package kbank.sandbox.dalmuti.controller;

import kbank.sandbox.dalmuti.game.business.GameManager;
import kbank.sandbox.dalmuti.message.business.MessageManager;
import kbank.sandbox.dalmuti.message.dto.MessageForm;
import kbank.sandbox.dalmuti.user.business.UserManager;
import kbank.sandbox.dalmuti.game.dto.GamerForm;
import kbank.sandbox.dalmuti.game.dto.GameForm;
import kbank.sandbox.dalmuti.game.dto.DeckForm;
import kbank.sandbox.dalmuti.game.dto.InGameForm;
import kbank.sandbox.dalmuti.user.dto.UserForm;
import kbank.sandbox.dalmuti.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import kbank.sandbox.dalmuti.game.exception.*;

import java.util.List;

import javax.validation.Valid;

/**
 * <pre>
 * 파 일 명 : DalmutiController.java
 * 설    명 : 달무티 컨트롤러
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by KBANK. All rights reserved.
 * </pre>
 */
@Controller
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/dalmuti")
public class DalmutiController {

    private static final Logger logger = LoggerFactory.getLogger(DalmutiController.class);

    private final UserManager userManager;

    private final GameManager gameManager;

    private final MessageManager messageManager;

    /**
     * 로그인 처리를 수행한다.
     * 
     * @param : userForm 사용자 정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/login")
    public JSONObject loginUser(@RequestBody @Valid UserForm userForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", userForm.toString());
        }

        UserForm result = userManager.loginUser(userForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "로그인 되었습니다.");

        return jsonObject;
    }

    /**
     * 새 게임을 생성한다.
     * 
     * @param : gameForm 게임 정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/newgame")
    public JSONObject newGame(@RequestBody @Valid InGameForm ingameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", ingameForm.toString());
        }

        GameForm result = gameManager.newGame(ingameForm);

        JSONObject jsonObject = new JSONObject();

        logger.error("request form: {}", result.toString());

        jsonObject.put("result", result);
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "게임을 생성하였습니다.");

        return jsonObject;
    }

    /**
     * 게임 시작 단계 : 카드를 pickup 한다.
     *
     * @param : game ID 및 gamer 대상 user list
     * @return : 결과코드, 메시지
     */
    @PostMapping("/pickcard")
    public JSONObject pickCard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        int rank = gameManager.pickCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rank",Utils.lpad(String.valueOf(rank),2,"0"));
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "카드를 선택하였습니다.");

        return jsonObject;
    }

    /**
     * 게임 시작 : 카드를 shuffle 하여 분배한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/shufflecard")
    public JSONObject shuffleCard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.shuffleCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "카드를 분배합니다.");

        return jsonObject;
    }

    /**
     * 카드 확인 : 분배된 카드를 확인한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/confirmcard")
    public JSONObject confirmCard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.confirmCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "카드를 확인합니다.");

        return jsonObject;
    }

    /**
     * 혁명 발생 : 혁명을 일으킵니다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/revolcard")
    public JSONObject revolCard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.revolCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "혁명이 발생했습니다.");

        return jsonObject;
    }

    /**
     * 세금 수령 : 세금을 수령합니다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/taxcard")
    public JSONObject taxCard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.taxCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "세금을 수령합니다.");

        return jsonObject;
    }

    /**
     * 세금 수령 : 세금을 면제합니다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/taxfreecard")
    public JSONObject taxfreecard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.taxFreeCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "세금을 면제합니다.");

        return jsonObject;
    }

    /**
     * 패스 : 턴을 패스합니다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/passcard")
    public JSONObject passcard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.passCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "턴을 패스합니다.");

        return jsonObject;
    }

    /**
     * 카드 제출 : 카드를 제출합니다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/drawcard")
    public JSONObject drawcard(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.drawCard(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "카드를 제출합니다.");

        return jsonObject;
    }

    /**
     * 게임종료 : 해당 게임의 결과를 확인하고 종료한다.(다음 게임을 위한 초기화)
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/confirmgame")
    public JSONObject confirmgame(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        gameManager.confirmGame(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "게임 결과를 확인합니다.");

        return jsonObject;
    }

    /**
     * 게임 참여 가능 검증
     *
     * @param : engageGameForm
     * @return : 결과코드, 메시지
     */
    @PostMapping("/validengagegame")
    public JSONObject validEngageGame(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        JSONObject jsonObject = new JSONObject();

        try {
            GamerForm result = gameManager.validEngageGame(inGameForm);

            jsonObject.put("result", result);
            jsonObject.put("resultCode", "000");
            jsonObject.put("message", "게임에 참여했습니다.");
    
            return jsonObject;
    
        } catch (IllegalGameAlreadyStartException e){
            jsonObject.put("result", null);
            jsonObject.put("resultCode", "001");
            jsonObject.put("message", "이미 시작된 게임은 참가할 수 없습니다.");
    
            return jsonObject;

        } catch (IllegalGamerCountException e) {
            jsonObject.put("result", null);
            jsonObject.put("resultCode", "002");
            jsonObject.put("message", "게임 가능인원 8명을 초과했습니다. ");
    
            return jsonObject;
        }
    }
    


    /**
     * 게임 참여
     *
     * @param : engageGameForm
     * @return : 결과코드, 메시지
     */
    @PostMapping("/engagegame")
    public JSONObject engageGame(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        JSONObject jsonObject = new JSONObject();

        try {
            GamerForm result = gameManager.engageGame(inGameForm);

            jsonObject.put("result", result);
            jsonObject.put("resultCode", "000");
            jsonObject.put("message", "게임에 참여했습니다.");
    
            return jsonObject;
    
        } catch (IllegalGameAlreadyStartException e){
            jsonObject.put("result", null);
            jsonObject.put("resultCode", "001");
            jsonObject.put("message", "이미 시작된 게임은 참가할 수 없습니다.");
    
            return jsonObject;

        } catch (IllegalGamerCountException e) {
            jsonObject.put("result", null);
            jsonObject.put("resultCode", "002");
            jsonObject.put("message", "게임 가능인원 8명을 초과했습니다. ");
    
            return jsonObject;
        }

    }

    /**
     * 대기룸 정보조회
     *
     * @param : engageGameForm
     * @return : 결과코드, 메시지
     */
    @PostMapping("/waitinfo")
    public JSONObject waitInfo(@RequestBody @Valid InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameForm);
        }

        List<GamerForm> result = gameManager.getWaitRoomGamerList(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "게임 조회에 성공했습니다.");

        return jsonObject;
    }

    /**
     * 게임 참여
     *
     * @param : engageGameForm
     * @return : 결과코드, 메시지
     */
    @PostMapping("/pickcardstart")
    public JSONObject pickCardStart(@RequestBody @Valid InGameForm inGameForm) {
        logger.error("request form: {}", inGameForm);
        
        gameManager.pickCardStart(inGameForm);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "카드선택이 시작되었습니다.");

        return jsonObject;
    }

    /**
     * 게임룸 정보조회
     *
     * @param : engageGameForm
     * @return : 결과코드, 메시지
     */
    @PostMapping("/gameinfo")
    public JSONObject gameInfo(@RequestBody @Valid InGameForm inGameform) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", inGameform);
        }

        List<GamerForm> gamerList = gameManager.getWaitRoomGamerList(inGameform);

        List<DeckForm> deckList = gameManager.getGamerDeckList(inGameform);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gamerList", gamerList);
        jsonObject.put("deckList", deckList);
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "게임 조회에 성공했습니다.");

        return jsonObject;
    }

    /**
     * 텔레그램을 전송한다.
     *
     * @param : userForm 사용자 정보
     * @return : 결과코드, 메시지
     */
    @PostMapping("/sendtelegram")
    public JSONObject sendTelegram(@RequestBody @Valid MessageForm messageForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("request form: {}", messageForm.toString());
        }

        messageManager.sendMessage(messageForm);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "텔레그램 전송되었습니다.");

        return jsonObject;
    }
    
}
