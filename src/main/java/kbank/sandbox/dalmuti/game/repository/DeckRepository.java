package kbank.sandbox.dalmuti.game.repository;

import kbank.sandbox.dalmuti.game.domain.Deck;
import kbank.sandbox.dalmuti.game.domain.Gamer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * 파 일 명 : DeckRepository.java
 * 설    명 : 덱 Entity 처리 Bean
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
public interface DeckRepository extends JpaRepository<Deck, String> {

    /**
     * 덱 ID 조회
     *
     * @param : deckId 덱 ID
     * @return : boolean 기등록여부 True/False
     */
    boolean existsByDeckId(String deckId);

    /**
     * 게임 참가자 기준 조회
     *
     * @param : gamer 게임 참가자 Entity
     * @return : List<Deck> 게임 참가자 기준 대상 덱 list
     */
    List<Deck> findByGamer(Gamer gamer, Sort sort);

}
