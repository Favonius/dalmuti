package kbank.sandbox.dalmuti.game.service;

import kbank.sandbox.dalmuti.game.repository.GameRepository;
import kbank.sandbox.dalmuti.game.domain.Game;
import kbank.sandbox.dalmuti.game.dto.GameForm;
import kbank.sandbox.dalmuti.game.exception.IllegalGameIdException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * <pre>
 * 파 일 명 : GameService.java
 * 설    명 : 게임 서비스
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    /**
     * 새 게임을 생성한다.
     * 
     * @param : gameForm 게임 정보
     * @return :
     * 1. 게임 ID 생성(숫자 + 영문 : 6자리)
     * 2. 게임 등록
     */
    @Transactional
    public GameForm createGame(GameForm gameForm) {
        // 1. 게임 ID 생성(숫자 + 영문 : 6자리)
        String gameId = generateGameId();

        if(logger.isDebugEnabled()) {
            logger.debug("게임 ID : {}", gameId);
        }

        logger.error("게임 ID : {}", gameId);

        // 2. 게임 등록
        gameForm.setGameId(gameId);
        gameRepository.save(Game.createGame(gameForm));

        return gameForm;
    }

    /**
     * 해당 게임의 정보를 조회한다.
     *
     * @param : gameForm 게임 정보
     * @return : GameForm 게임 정보
     * 1. 게임 기 등록여부 점검(미 등록시 Exception) 및 조회
     */
    @Transactional
    public GameForm selectGame(GameForm gameForm) {
        // 1. 게임 기 등록여부 점검(미 등록시 Exception) 및 조회
        return GameForm.convert(gameRepository.findById(gameForm.getGameId()).orElseThrow(IllegalGameIdException::new));
    }

    /**
     * 게임 정보를 수정한다.
     *
     * @param : gameForm 게임 정보
     * @return :
     * 3. 게임 정보를 수정
     */
    @Transactional
    public void updateGame(GameForm gameForm) {
        gameRepository.save(Game.createGame(gameForm));
    }

    /**
     * 게임 ID를 생성한다.
     * 
     * @param :
     * @return : gameId 게임 ID
     * 1. 게임 ID 생성(숫자 + 영문 : 6자리)
     * 2. 게임 ID 기 등록여부 점검(기 등록시 재귀호출)
     */
    private String generateGameId() {
        // 1. 게임 ID 생성(숫자 + 영문 : 6자리)
        String gameId = RandomStringUtils.randomAlphanumeric(6).toUpperCase();

        // 2. 게임 ID 기 등록여부 점검(기 등록시 재귀호출)
        if(gameRepository.existsByGameId(gameId)) {
            gameId = generateGameId();
        }

        return gameId;
    }
}
