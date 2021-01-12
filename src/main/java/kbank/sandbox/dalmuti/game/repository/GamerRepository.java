package kbank.sandbox.dalmuti.game.repository;

import kbank.sandbox.dalmuti.game.domain.Game;
import kbank.sandbox.dalmuti.game.domain.Gamer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * 파 일 명 : GamerRepository.java
 * 설    명 : 게이머 Entity 처리 Bean
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
public interface GamerRepository extends JpaRepository<Gamer, String> {

    /**
     * 게임 참가자 ID 조회
     *
     * @param : gamerId 게임 참가자 ID
     * @return : boolean 기등록여부 True/False
     */
    boolean existsByGamerId(String gamerId);

    /**
     * 게임 ID 기준 조회
     *
     * @param : game 게임 Entity
     * @return : List<Gamer> game 기준 대상 게이머 list
     */
    List<Gamer> findByGame(Game game, Sort sort);

    /**
     * 게임 ID / Rank 기준 조회
     *
     * @param : gamerId 게임 참가자 ID
     * @return : boolean 기등록여부 True/False
     */
    List<Gamer> findByGameAndRank(Game game, int rank, Sort sort);

    /**
     * 게임 ID / Next Rank 기준 조회
     *
     * @param : game 게임 Entity, nxtRank Next Rank
     * @return : List<Gamer> game / Next Rank 기준 대상 게이머 list
     */
    List<Gamer> findByGameAndNxtRank(Game game, int nxtRank, Sort sort);

    /**
     * 게임 ID / Status 기준 조회
     *
     * @param : game 게임 Entity, status 게임 참가자 상태
     * @return : List<Gamer> game / 게임 참가자 상태 기준 대상 게이머 list
     */
    List<Gamer>  findByGameAndStatus(Game game, int status, Sort sort);
}
