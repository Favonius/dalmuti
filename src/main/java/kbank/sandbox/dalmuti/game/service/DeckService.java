package kbank.sandbox.dalmuti.game.service;

import kbank.sandbox.dalmuti.game.domain.Deck;
import kbank.sandbox.dalmuti.game.exception.IllegalDeckIdException;
import org.springframework.data.domain.Sort;
import kbank.sandbox.dalmuti.game.dto.DeckForm;
import kbank.sandbox.dalmuti.game.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * <pre>
 * 파 일 명 : DeckService.java
 * 설    명 : 덱 서비스
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class DeckService {

    private static final Logger logger = LoggerFactory.getLogger(DeckService.class);

    private final DeckRepository deckRepository;

    /**
     * 덱 정보를 입력한다.
     *
     * @param : deckForm 덱 정보
     * @return :
     * 1. 덱 ID 생성(숫자 + 영문 : 10자리)
     * 2. 덱 생성
     */
    @Transactional
    public void createDeck(DeckForm deckForm) {
        // 1. 덱 ID 생성(숫자 + 영문 : 10자리)
        String deckId = generateDeckId();

        if(logger.isDebugEnabled()) {
            logger.debug("덱 ID : {}", deckId);
        }

        // 2. 덱 생성
        deckForm.setDeckId(deckId);
        deckRepository.save(Deck.createDeck(deckForm));
    }

    /**
     * 해당 덱 정보를 조회한다.
     *
     * @param : deckForm 덱 정보
     * @return : DeckForm 덱 정보
     * 1. 덱 기 등록여부 점검(미 등록시 Exception) 및 조회
     */
    @Transactional
    public DeckForm selectDeck(DeckForm deckForm) {
        // 1. 덱 기 등록여부 점검(미 등록시 Exception) 및 조회
        return DeckForm.convert(deckRepository.findById(deckForm.getDeckId()).orElseThrow(IllegalDeckIdException::new));
    }

    /**
     * 해당 게임 참가자의 덱 전체 정보를 조회한다.
     *
     * @param : deckForm 덱 정보
     * @return : List<DeckForm> 기준 대상 덱 list
     * 1. 게임 참가자 기준으로 대상 덱 list 조회
     */
    @Transactional
    public List<DeckForm> selectDeckList(DeckForm deckForm) {
        // 1. 게임 참가자 기준으로 대상 덱 list 조회
        return DeckForm.convert(deckRepository.findByGamer(deckForm.getGamer(), sortAscByStr("card")));
    }

    /**
     * 해당 덱 정보를 수정한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return :
     * 1. 덱 정보를 수정
     */
    @Transactional
    public void updateDeck(DeckForm deckForm) {
        // 1. 덱 정보를 수정
        deckRepository.save(Deck.createDeck(deckForm));
    }

    /**
     * 해당 덱을 삭제한다.
     *
     * @param : deckForm 덱 정보
     * @return :
     * 1. 덱 list의 id를 기준으로 삭제
     */
    @Transactional
    public void deleteDeck(DeckForm deckForm) {
        // 1. 덱 list의 id를 기준으로 일괄 삭제
        deckRepository.delete(Deck.createDeck(deckForm));
    }

    /**
     * 덱 ID를 생성한다.
     *
     * @param :
     * @return : deckId 덱 ID
     * 1. 덱 ID 생성(숫자 + 영문 : 10자리)
     * 2. 덱 ID 기 등록여부 점검(기 등록시 재귀호출)
     */
    private String generateDeckId() {
        // 1. 덱 ID 생성(숫자 + 영문 : 10자리)
        String deckId = RandomStringUtils.randomAlphanumeric(10).toUpperCase();

        // 2. 덱 ID 기 등록여부 점검(기 등록시 재귀호출)
        if(deckRepository.existsByDeckId(deckId)) {
            deckId = generateDeckId();
        }

        return deckId;
    }

        /**
     * 정렬 객체를 생성한다.
     *
     * @param :
     * @return : Sort str 기준 ASC 정렬
     */
    private Sort sortAscByStr(String sort) {
        return Sort.by(Sort.Direction.ASC, sort);
    }
}
