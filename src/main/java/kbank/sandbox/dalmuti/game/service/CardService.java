package kbank.sandbox.dalmuti.game.service;

import kbank.sandbox.dalmuti.game.dto.CardForm;
import kbank.sandbox.dalmuti.game.domain.Card;
import kbank.sandbox.dalmuti.game.exception.IllegalCardIdException;
import kbank.sandbox.dalmuti.game.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * <pre>
 * 파 일 명 : CardService.java
 * 설    명 : 카드 서비스
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;

    /**
     * 카드를 조회한다.
     *
     * @param : cardForm 카드 정보
     * @return : CardForm 카드 정보
     * 1. 카드 기 등록여부 점검(미 등록시 Exception)
     */
    @Transactional
    public CardForm selectCard(CardForm cardForm) {
        // 1. 카드 기 등록여부 점검(미 등록시 Exception)
        return CardForm.convert(cardRepository.findById(cardForm.getCardId()).orElseThrow(IllegalCardIdException::new));
    }

    /**
     * 카드를 전체 조회한다.
     *
     * @param : deckForm 덱 정보
     * @return : List<CardForm> 카드 정보 list
     * 1. 카드 전체 list 조회
     */
    @Transactional
    public List<CardForm> selectAllCard() {
        // 1. 카드 전체 list 조회
        return CardForm.convert(cardRepository.findAll());
    }

    /**
     * 카드를 생성한다
     *
     * @param : cardForm 카드 정보
     * @return : CardForm 카드 정보
     * 1. 카드 기 등록여부 점검(미 등록시 Exception)
     */
    @Transactional
    public void saveCard(CardForm cardForm) {

        if(logger.isDebugEnabled()) {
            logger.debug("card : {}", cardForm.toString());
        }

        cardRepository.save(Card.createCard(cardForm));
    }

}
