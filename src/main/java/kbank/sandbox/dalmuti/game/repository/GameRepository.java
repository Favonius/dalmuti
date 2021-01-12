package kbank.sandbox.dalmuti.game.repository;

import kbank.sandbox.dalmuti.game.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * 파 일 명 : GameRepository.java
 * 설    명 : 게임 Entity 처리 Bean
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
public interface GameRepository extends JpaRepository<Game, String> {

    /**
     * 게임 ID 조회
     *
     * @param : gameId 게임 ID
     * @return : boolean 기등록여부 True/False
     */
    boolean existsByGameId(String gameId);
}
