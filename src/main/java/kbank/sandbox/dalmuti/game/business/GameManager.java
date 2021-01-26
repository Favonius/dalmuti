package kbank.sandbox.dalmuti.game.business;

import kbank.sandbox.dalmuti.game.domain.Card;

import kbank.sandbox.dalmuti.game.domain.Game;
import kbank.sandbox.dalmuti.game.domain.Gamer;
import kbank.sandbox.dalmuti.game.dto.*;
import kbank.sandbox.dalmuti.game.enums.CardRankEnum;
import kbank.sandbox.dalmuti.game.enums.GameCodeEnum;
import kbank.sandbox.dalmuti.game.enums.GamerStatusEnum;
import kbank.sandbox.dalmuti.game.exception.*;
import kbank.sandbox.dalmuti.game.service.CardService;
import kbank.sandbox.dalmuti.game.service.DeckService;
import kbank.sandbox.dalmuti.game.service.GameService;
import kbank.sandbox.dalmuti.game.service.GamerService;
import kbank.sandbox.dalmuti.user.business.UserManager;
import kbank.sandbox.dalmuti.user.domain.User;
import kbank.sandbox.dalmuti.user.dto.UserForm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.*;

/**
 * <pre>
 * 파 일 명 : GameManager.java
 * 설    명 : 게임 진행 관리
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class GameManager {

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    private final UserManager userManager;

    private final GameService gameService;

    private final GamerService gamerService;

    private final CardService cardService;

    private final DeckService deckService;

    /**
     * 새 게임을 생성한다.
     *
     * @param : gameForm 게임 정보
     * @return :
     * 1. 게임 생성
     */
    @Transactional
    public GameForm newGame(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("newGame info: {}", inGameForm.toString());
        }

        // 1. 게임 생성
        return gameService.createGame(GameForm.convert(""));
    }

    /**
     * 선을 정하기 위해 카드를 선택한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 전달받은 참가자의 기존 정보 조회
     * 2. 선택한 카드의 유효성 검사(기 선택 카드여부 확인-동일 계급은 1장씩만 선택 가능)
     * 3. 해당 게임 참가자의 rank(순서)를 설정한다.
     */
    @Transactional
    public int pickCard(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("pickCard info: {}", inGameForm.toString());
        }

        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        Random rd = new Random();//랜덤 객체 생성
        
        boolean findRank = true;

        int rank = 0;
        while(findRank) {

            findRank = false;
            rank = rd.nextInt(12)+1;
            
            List<GamerForm> gamerFormList = gamerService.selectGamerList(gamerForm);

            for(int i = 0; i<gamerFormList.size();i++) {
                if( gamerFormList.get(i).getRank() == rank ) {
                    findRank = true;
                    break;
                }
            }
        }
        gamerForm.setRank(rank);
        gamerService.updateGamer(gamerForm);

        return rank;
        
    }

    /**
     * 게임 진행을 위한 카드를 분배한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 분배대상 카드 생성
     * 1.1. 전체 카드에 대해 조회(총 80장) 및 shuffle
     * 2. 분배 전 상태 점검
     * 2.1. 카드분배 권한 확인(순위가 1순위인 참가자만 가능)
     * 2.2. 해당 게임 참가자의 rank 값 확인(0은 없어야 함)
     * 2.3. deck에 존재하는 해당 게임 참가자의 카드 삭제
     * 3. 게임 참가자 순서 정비(1~12 값을 1부터 참가자수(5~8)까지로 정비하여 수정
     * 4. shuffle된 카드를 참가자 순서(rank)에 맞게 저장
     * 5. 게임 참가자 카드 및 조커 개수 변경
     * 6. 게임 참가자 turn 여부 변경(첫번째 계급)
     * 7. 게임 상태값 변경(게임준비 -> 카드확인)(last user 변경)
     */
    @Transactional
    public void shuffleCard(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("shuffleCard info: {}", inGameForm.toString());
        }
        // 1.카드 생성
        createCard();
        
        // 1.1. 전체 카드에 대해 조회(총 80장) 및 shuffle
        List<CardForm> cardFormList = cardService.selectAllCard();
        Collections.shuffle(cardFormList);

        // 2. 분배 전 상태 점검
        GamerForm inGamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));
        List<GamerForm> gamerFormList =  gamerService.selectGamerList(inGamerForm);

        if(inGamerForm.getGame().getGameCode() != GameCodeEnum.READY.ordinal()) {
            throw new IllegalGamerStatusException();
        }
        // 2.1. 카드분배 권한 확인(순위가 1순위인 참가자만 가능)
        // if(inGamerForm.getRank() != 1 || inGamerForm.getStatus() != GamerStatusEnum.WAIT.ordinal()) {
        //     throw new IllegalGamerRankException();
        // }

        logger.debug("gamer size info: {}", gamerFormList.size());
        
        for(int i = 0; i<gamerFormList.size(); i++) {
            logger.debug("gamer info: {}", gamerFormList.toString());
        }

        List<DeckForm> deckFormList;
        DeckForm deckForm = new DeckForm();

        int gamerCount = gamerFormList.size();
        int rank = 0;
        int index = 0, jokerCnt, cardTotCnt;

        int cardCnt = 80 / gamerCount;

        logger.debug("cardCnt info: {}", cardCnt);

        // 2.2. 해당 게임 참가자의 rank 값 확인(0은 없어야 함)
        for(GamerForm gamerForm:gamerFormList) {
            if(gamerForm.getRank() == 0) {
                throw new IllegalGamerRankException();
            }

            // 2.3. deck에 존재하는 해당 게임 참가자의 카드 삭제
            deckForm.setGamer(Gamer.createGamer(gamerForm));
            deckFormList = deckService.selectDeckList(deckForm);
            for(DeckForm deleteDeckForm:deckFormList) {
                deckService.deleteDeck(deleteDeckForm);
            }

            // 3. 게임 참가자 순서 정비(1~12 값을 1부터 참가자수(5~8)까지로 정비하여 수정
            gamerForm.setRank(++rank);

            // 4. shuffle된 카드를 참가자 순서(rank)에 맞게 저장
            // index = rank - 1;
            // jokerCnt = 0;
            // cardTotCnt = 0;
            // while(index < 80) {
            //     deckForm.setCard(Card.createCard(cardFormList.get(index)));
            //     deckService.createDeck(deckForm);
            //     if(deckForm.getCard().getRank() == 13) {
            //         jokerCnt += 1;
            //     }
            //     cardTotCnt += 1;
            //     index = index + gamerCount;
            // }
            jokerCnt = 0;
            cardTotCnt = 0;
            
            index++;

            for(int i = 0; i < cardCnt ; i ++) {
                int k = (index-1) * cardCnt + i;

                logger.debug("card info: {}", cardFormList.get(k).toString());
                deckForm.setCard(Card.createCard(cardFormList.get(k)));

                deckService.createDeck(deckForm);
                if(deckForm.getCard().getRank() == CardRankEnum.JESTER.ordinal()) {
                    jokerCnt += 1;
                }
                cardTotCnt += 1;
            }

            if(index <= 80 % gamerFormList.size()) {

                int remainderCardIndex = 80 - 80%gamerFormList.size() + index - 1;

                deckForm.setCard(Card.createCard(cardFormList.get(remainderCardIndex)));
                deckService.createDeck(deckForm);

                if(deckForm.getCard().getRank() == CardRankEnum.JESTER.ordinal()) {
                    jokerCnt += 1;
                }
                cardTotCnt = cardCnt + 1;
            }

            // 5. 게임 참가자 카드 및 조커 개수 변경
            gamerForm.setJokerCnt(jokerCnt);
            gamerForm.setCardTotCnt(cardTotCnt);
            if(rank == 1) {
                gamerForm.setTurn(true);
            }
            gamerService.updateGamer(gamerForm);
        }

        // 6. 게임 참가자 turn 여부 변경(첫번째 계급)
        updateGamerTurn(inGamerForm);

        // 7. 게임 상태값 변경(게임준비 -> 카드확인)(last user 변경)
        updateGameCode(GameForm.convert(inGamerForm.getGame()));
        updateGameLast(inGamerForm);
    }

    /**
     * 분배된 카드를 확인한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 2. 게임 참가자 상태값 변경(카드대기 -> 카드확인)
     * 3. 게임 참가자 전체 상태 확인(카드대기 상태인 참가자 확인)
     * 3.1. 카드대기 상태가 없는 경우(전체 참가자 카드확인 완료 시), 게임 상태값 변경(카드확인 -> 세금징수)
     */
    @Transactional
    public void confirmCard(InGameForm inGameForm) {
        if (logger.isDebugEnabled()) {
            logger.debug("confirmCard info: {}", inGameForm.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        if(gamerForm.getStatus() != GamerStatusEnum.WAIT.ordinal()) {
            throw new IllegalGamerStatusException();
        }

        // 2. 게임 참가자 상태값 변경(카드대기 -> 카드확인)
        updateGamerStatus(gamerForm);

        // 2.1. 세금 대상이 아닌 경우, 게임 참가자 상태값 변경(세금징수 -> 게임진행)
        List<GamerForm> gamerList =  gamerService.selectGamerList(gamerForm);
        if(gamerForm.getRank() > 2 && gamerForm.getRank() < gamerList.size() - 1) {
            updateGamerStatus(gamerForm);
        }

        // 3. 게임 참가자 전체 상태 확인(카드대기 상태인 참가자 확인)
        List<GamerForm> gamerFormList =  gamerService.selectGamerStatusList(gamerForm);

        if(gamerFormList.isEmpty()) {
            // 3.1. 카드대기 상태가 없는 경우(전체 참가자 카드확인 완료 시), 게임 상태값 변경(카드확인 -> 세금징수)
            updateGameCode(GameForm.convert(gamerForm.getGame()));
        }
    }

    /**
     * 혁명 단계를 수행한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 2. 혁명 단계 진행
     * 2.1. 게임 참가자 레볼루션 여부 update
     * 2.2. 노예 반란
     * 2.2.1. 게임 참가자 순서 역순 정비(노예가 왕으로, 왕이 노예로)
     * 2.2.2. 게임 참가자 순서 변경(노예가 왕으로, 왕이 노예로)
     * 2.3. 게임 참가자 상태값 변경(카드대기, 세금징수 -> 게임진행)
     * 2.3.1. 게임참가자 순서에 따른 선 설정(1등)
     * 2.3.2. 게임 최종 제출자 기본 설정
     * 2.4. 게임 상태값 변경(카드확인 -> 세금징수 -> 게임진행)
     * 2.4.1. 게임 상태값 변경(카드확인 -> 세금징수)
     * 2.4.2. 게임 상태값 변경(세금징수 -> 게임진행)
     */
    @Transactional
    public void revolCard(InGameForm inGameForm) {
        if (logger.isDebugEnabled()) {
            logger.debug("revolCard info: {}", inGameForm.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        if(gamerForm.getJokerCnt() < 2 || gamerForm.getStatus() != GamerStatusEnum.WAIT.ordinal()) {
            throw new IllegalGamerRevolutionException();
        }

        // 2. 혁명 단계 진행
        // 2.1. 게임 참가자 레볼루션 여부 update
        gamerForm.setRevolution(true);
        gamerService.updateGamer(gamerForm);

        List<GamerForm> gamerFormList =  gamerService.selectGamerList(gamerForm);

        // 2.2. 노예 반란
        if(gamerForm.getRank() == gamerFormList.size()) {
            // 2.2.1. 게임 참가자 순서 역순 정비(노예가 왕으로, 왕이 노예로)
            Collections.sort(gamerFormList, Collections.reverseOrder());
        }

        int rank = 0;

        for(GamerForm gamerRankForm:gamerFormList) {
            // 2.2.2. 게임 참가자 순서 변경(노예 반란인 경우는 노예가 왕으로, 왕이 노예로)
            gamerRankForm.setRank(++rank);
            // 2.3. 게임 참가자 상태값 변경(카드대기, 세금징수 -> 게임진행)
            gamerRankForm.setStatus(GamerStatusEnum.PLAY.ordinal());
            // 2.3.1. 게임참가자 순서에 따른 선 설정(1등)
            if(rank == 1) {
                gamerRankForm.setTurn(true);
                // 2.3.2. 게임 최종 제출자 기본 설정
                GameForm gameForm = gameService.selectGame(GameForm.convert(gamerRankForm.getGame()));
                gameForm.setLastUser(gamerRankForm.getUser());
                gameService.updateGame(gameForm);
            } else {
                gamerRankForm.setTurn(false);
            }
            gamerService.updateGamer(gamerRankForm);
        }

        // 2.4. 게임 상태값 변경(카드확인 -> 세금징수 -> 게임진행)
        // 2.4.1. 게임 상태값 변경(카드확인 -> 세금징수)
        gamerForm.setGame(Game.createGame(updateGameCode(GameForm.convert(gamerForm.getGame()))));
        // 2.4.2. 게임 상태값 변경(세금징수 -> 게임진행)
        updateGameCode(GameForm.convert(gamerForm.getGame()));
    }

    /**
     * 세금을 징수한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 1.1. 게임 참가자 세금수납 가능여부 확인(1, 2 rank 만 세금수납 가능)
     * 1.2. 세금 카드의 개수가 2장 이상이거나, 0장 이거나, rank에 따른 장수와 맞지 않으면 Exception
     * 2. 세금수납 대상 조회(1 rank - last rank, 2 rank - pre-last rank)
     * 3. 세금 수납
     * 3.1. 세금 피징수자의 대상 카드 조회
     * 3.2. 세금 피징수자의 카드를 세금 징수자의 deck으로 변경
     * 3.3. 세금 징수자의 카드를 세금 피징수자 deck으로 변경
     * 3.4. 조커를 주는 경우, 징수자 및 피징수자의 joker cnt 수정 필요
     * 4. 세금 (피)징수자 상태값 변경
     * 4.1. 세금 징수자 상태값 변경(세금징수 -> 게임진행)
     * 4.2. 세금 피징수자 상태값 변경(세금징수 -> 게임진행)
     * 5. 게임 참가자 전체 상태 확인(세금징수 상태인 참가자 확인)
     * 5.1. 세금징수 상태가 없는 경우(전체 참가자 카드확인 완료 시), 게임 상태값 변경(세금징수 -> 게임진행)
     */
    @Transactional
    public void taxCard(InGameForm inGameForm) {
        if (logger.isDebugEnabled()) {
            logger.debug("taxCard info: {}", inGameForm.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        if (logger.isDebugEnabled()) {
            logger.debug("taxCard gamerForm info: {}", gamerForm.toString());
        }

        // 1.1. 게임 참가자 세금수납 가능여부 확인(1, 2 rank 만 세금수납 가능)
        if(gamerForm.getRank() > 2 || gamerForm.getRank() < 1 || gamerForm.getStatus() != GamerStatusEnum.TAX.ordinal()) {
            throw new IllegalGamerTaxException();
        }

        // 1.2. 세금 카드의 개수가 2장 초과 혹은 1장 미만이거나, rank에 따른 장수와 맞지 않으면 Exception
        if(inGameForm.getDeckIds().size() > 2 - ((gamerForm.getRank() + 1) % 2)) {
            throw new IllegalGamerTaxException();
        }

        // 2. 세금수납 대상 조회(1 rank - last rank, 2 rank - pre-last rank)
        List<GamerForm> gamerTaxFormList =  gamerService.selectGamerStatusList(gamerForm);

        if (logger.isDebugEnabled()) {
            logger.debug("taxCard gamerTaxFormList info: {}", gamerTaxFormList.toString());
        }
        DeckForm taxDeckForm = new DeckForm();
        taxDeckForm.setGamer(Gamer.createGamer(gamerTaxFormList.get(gamerTaxFormList.size()>2?gamerTaxFormList.size() - gamerForm.getRank():1)));

        if (logger.isDebugEnabled()) {
            logger.debug("taxCard taxDeckForm info: {}", taxDeckForm.toString());
        }

        // 3. 세금수납
        // 3.1. 세금 피징수자의 대상 카드 조회
        List<DeckForm> deckFormList = new ArrayList<>(deckService.selectDeckList(taxDeckForm).subList(0, 3 - gamerForm.getRank()));

        // 3.2. 세금 피징수자의 카드를 세금 징수자의 deck으로 변경
        for(DeckForm deckForm: deckFormList) {
            deckForm.setGamer(Gamer.createGamer(gamerForm));
            deckForm.setTax(true);
            deckService.updateDeck(deckForm);
        }

        // 3.3. 세금 징수자의 카드를 세금 피징수자 deck으로 변경
        for(String deck: inGameForm.getDeckIds()) {

            DeckForm deckForm = deckService.selectDeck(DeckForm.convert(deck));

            if(!deckForm.getGamer().getGamerId().equals(gamerForm.getGamerId())) {
                throw new IllegalDeckIdException();
            }

            deckForm.setGamer(taxDeckForm.getGamer());
            deckForm.setTax(true);
            deckService.updateDeck(deckForm);

            // 3.4. 조커를 주는 경우, 징수자 및 피징수자의 joker cnt 수정 필요
            if(deckForm.getCard().getRank() == CardRankEnum.JESTER.ordinal()) {
                GamerForm jokerGamerForm = gamerService.selectGamer(gamerForm);

                if(jokerGamerForm.getJokerCnt() < 1) {
                    throw new IllegalDeckIdException();
                }

                jokerGamerForm.setJokerCnt(jokerGamerForm.getJokerCnt() - 1);
                gamerService.updateGamer(jokerGamerForm);

                GamerForm jokerTaxGamerForm = gamerService.selectGamer(GamerForm.convert(taxDeckForm.getGamer()));
                jokerTaxGamerForm.setJokerCnt(jokerTaxGamerForm.getJokerCnt() + 1);
                gamerService.updateGamer(jokerTaxGamerForm);
            }

            // 조커를 주는 경우, 징수자 및 피징수자의 joker cnt 수정 필요
        }

        // 4. 세금 (피)징수자 상태값 변경
        // 4.1. 세금 징수자 상태값 변경(세금징수 -> 게임진행)
        updateGamerStatus(gamerForm);

        // 4.2. 세금 피징수자 상태값 변경(세금징수 -> 게임진행)
        updateGamerStatus(GamerForm.convert(taxDeckForm.getGamer()));

        // 5. 게임 참가자 전체 상태 확인(세금징수 상태인 참가자 확인)
        gamerTaxFormList =  gamerService.selectGamerStatusList(gamerForm);
        if(gamerTaxFormList.isEmpty()) {
            // 5.1. 세금징수 상태가 없는 경우(전체 참가자 카드확인 완료 시), 게임 상태값 변경(세금징수 -> 게임진행)
            updateGameCode(GameForm.convert(gamerForm.getGame()));
            // 5.2. 세금징수 상태가 없는 경우(전체 참가자 카드확인 완료 시), 첫번째 랭크인 사람에게 게임턴 부여
            List<GamerForm> updateTurnGamerFormList =  gamerService.selectGamerList(gamerForm);

            if(updateTurnGamerFormList.size() > 0) {
                GamerForm updateTurnGamerForm = updateTurnGamerFormList.get(0);

                updateTurnGamerForm.setTurn(true);

                gamerService.updateGamer(updateTurnGamerForm);
            } 
        }
    }

    /**
     * 세금을 면제한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 1.1. 게임 참가자 세금수납 가능여부 확인(1, 2 rank 만 세금수납 가능)
     * 2. 세금면제 대상 조회(1 rank - last rank, 2 rank - pre-last rank)
     * 3. 세금 (피)징수자 상태값 변경
     * 3.1. 세금 징수자 상태값 변경(세금징수 -> 게임진행)
     * 3.2. 세금 피징수자 상태값 변경(세금징수 -> 게임진행)
     * 4. 게임 참가자 전체 상태 확인(세금징수 상태인 참가자 확인)
     * 4.1. 세금징수 상태가 없는 경우(전체 참가자 카드확인 완료 시), 게임 상태값 변경(세금징수 -> 게임진행)
     */
    @Transactional
    public void taxFreeCard(InGameForm inGameForm) {
        if (logger.isDebugEnabled()) {
            logger.debug("taxFreeCard info: {}", inGameForm.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        if (logger.isDebugEnabled()) {
            logger.debug("taxCard gamerForm info: {}", gamerForm.toString());
        }

        // 1.1. 게임 참가자 세금수납 가능여부 확인(1, 2 rank 만 세금수납 가능)
        if(gamerForm.getRank() > 2 || gamerForm.getRank() < 1 || gamerForm.getStatus() != GamerStatusEnum.TAX.ordinal()) {
            throw new IllegalGamerTaxException();
        }

        // 2. 세금면제 대상 조회(1 rank - last rank, 2 rank - pre-last rank)
        List<GamerForm> gamerTaxFormList =  gamerService.selectGamerStatusList(gamerForm);

        if (logger.isDebugEnabled()) {
            logger.debug("taxCard gamerTaxFormList info: {}", gamerTaxFormList.toString());
        }

        DeckForm taxDeckForm = new DeckForm();
        taxDeckForm.setGamer(Gamer.createGamer(gamerTaxFormList.get(gamerTaxFormList.size()>2?gamerTaxFormList.size() - gamerForm.getRank():1)));

        if (logger.isDebugEnabled()) {
            logger.debug("taxCard taxDeckForm info: {}", taxDeckForm.toString());
        }

        // 3. 세금 (피)징수자 상태값 변경
        // 3.1. 세금 징수자 상태값 변경(세금징수 -> 게임진행)
        updateGamerStatus(gamerForm);

        // 3.2. 세금 피징수자 상태값 변경(세금징수 -> 게임진행)
        updateGamerStatus(GamerForm.convert(taxDeckForm.getGamer()));

        // 4. 게임 참가자 전체 상태 확인(세금징수 상태인 참가자 확인)
        gamerTaxFormList =  gamerService.selectGamerStatusList(gamerForm);
        if(gamerTaxFormList.isEmpty()) {
            // 4.1. 세금징수 상태가 없는 경우(전체 참가자 카드확인 완료 시), 게임 상태값 변경(세금징수 -> 게임진행)
            updateGameCode(GameForm.convert(gamerForm.getGame()));

            // 4.2. 세금징수 상태가 없는 경우(전체 참가자 카드확인 완료 시), 첫번째 랭크인 사람에게 게임턴 부여
            List<GamerForm> updateTurnGamerFormList =  gamerService.selectGamerList(gamerForm);

            if(updateTurnGamerFormList.size() > 0) {
                GamerForm updateTurnGamerForm = updateTurnGamerFormList.get(0);

                updateTurnGamerForm.setTurn(true);

                gamerService.updateGamer(updateTurnGamerForm);
            } 
        }
    }

    /**
     * 턴을 패스한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 2. 패스 가능 여부 점검
     * 2.1. 패스 가능 여부 점검(현재 턴이 아니거나, 게임진행중이 아닌 경우 패스 불가)
     * 2.2. 패스 가능 여부 점검(선인 경우 패스 불가)
     * 3. 게임 참가자 turn 여부 변경
     */
    @Transactional
    public void passCard(InGameForm inGameForm) {
        if (logger.isDebugEnabled()) {
            logger.debug("passCard info: {}", inGameForm.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        // 2. 패스 가능 여부 점검
        // 2.1. 패스 가능 여부 점검(현재 턴이 아니거나, 게임진행중이 아닌 경우 패스 불가)
        if(!gamerForm.isTurn() || gamerForm.getGame().getGameCode() != GameCodeEnum.PLAY.ordinal()) {
            throw new IllegalGamerTurnException();
        }

        // 2.2. 패스 가능 여부 점검(선인 경우 패스 불가)
        if(gamerForm.getUser().equals(gamerForm.getGame().getLastUser())) {
            throw new IllegalGamerLastUserException();
        }

        // 3. 게임 참가자 turn 여부 변경
        updateGamerTurn(gamerForm);
    }

    /**
     * 카드를 제출한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 2. 제출 카드 처리
     * 2.1. 제출 카드 여부 점검(본인 카드만 제출)
     * 2.2. 제출 카드 유효성 점검(카드 숫자 혼합여부 점검)
     * 2.3. 제출 카드 덱 제외 처리
     * 2.4. 제출 카드 유효성 점검(선인 경우 제외)
     * 2.4.1. 제출 카드 유효성 점검(기 제출카드 보다 rank가 높아야 함)
     * 2.4.2. 제출 카드 유효성 점검(기 제출카드와 장수가 같아야 함)
     * 3. 게임 상태 변경(최종 카드, 숫자, 조커, 사용자)
     * 4. 게임 참가자 상태 변경
     * 4.1. 게임 참가자 turn 여부 변경
     * 4.1.1. 제출 카드가 달무티 혹은 Rank 보다 큰 경우는 턴 유지(최종 제출시에만 턴 변경, 이 경우, LastUser도 변경)
     * 4.1.2. 일반 제출의 경우, 턴 변경
     * 4.2. 게임 참가자 카드 개수 변경
     * 4.3. 게임 참가자 카드가 모두 제출된 경우, 결과 Rank 값 변경
     * 4.4. 게임 참가자 상태값 변경(게임진행 -> 게임종료)
     * 5. 최종 제출여부 점검(제출 후, 잔여 카드 개수 있는 참가자가 1명만 남은 경우)
     * 5.1. 게임 상태값 변경(게임진행 -> 게임종료)
     * 5.2. 잔여 게임 참가자 상태 변경(게임진행 -> 게임종료)
     * 5.3. 잔여 카드 삭제
     * 5.4. 전체 게이머 Rank 변경(Next Rank -> Rank)
     */
    @Transactional
    public void drawCard(InGameForm inGameForm) {
        if (logger.isDebugEnabled()) {
            logger.debug("drawCard info: {}", inGameForm.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        if(!gamerForm.isTurn() || gamerForm.getGame().getGameCode() != GameCodeEnum.PLAY.ordinal()) {
            logger.error("IllegalDeckIdException : {}", gamerForm.toString());
            throw new IllegalGamerTurnException();
        }

        int lastCardRank = CardRankEnum.JESTER.ordinal();
        int lastJokerCnt = 0;

        // 2. 제출 카드 처리
        for(String deck: inGameForm.getDeckIds()) {
            // 2.1. 제출 카드 여부 점검(본인 카드만 제출)
            DeckForm deckForm = deckService.selectDeck(DeckForm.convert(deck));

            logger.debug("deckForm : {}", deckForm);
            
            if(!deckForm.getGamer().getGamerId().equals(gamerForm.getGamerId())) {
                logger.error("IllegalDeckIdException : {}", gamerForm.toString());
                throw new IllegalDeckIdException();
            }

            // 2.2. 제출 카드 유효성 점검(카드 숫자 혼합여부 점검)
            if(deckForm.getCard().getRank() == CardRankEnum.JESTER.ordinal()) {
                lastJokerCnt += 1;
            } else if(lastCardRank == CardRankEnum.JESTER.ordinal()) {
                lastCardRank = deckForm.getCard().getRank();
            } else if(deckForm.getCard().getRank() != lastCardRank) {
                logger.error("IllegalCardDrawException : {} ", deckForm.toString());
                logger.error("IllegalCardDrawException : {} ", lastCardRank);

                throw new IllegalCardDrawException();
            }

            // 2.3. 제출 카드 덱 제외 처리
            deckService.deleteDeck(deckForm);
        }

        // 2.4. 제출 카드 유효성 점검(선인 경우 제외)
        if(!gamerForm.getUser().equals(gamerForm.getGame().getLastUser())) {
            // 2.4.1. 제출 카드 유효성 점검(기 제출카드 보다 rank가 높아야 함)
            if(lastCardRank >= gamerForm.getGame().getLastCardRank()) {
                logger.error("IllegalCardDrawException : {}", gamerForm.toString());
                throw new IllegalCardDrawException();
            }
            // 2.4.2. 제출 카드 유효성 점검(기 제출카드와 장수가 같아야 함)
            if(inGameForm.getDeckIds().size() != gamerForm.getGame().getLastCardCnt()) {
                logger.error("IllegalCardDrawException : {}", gamerForm.toString());
                throw new IllegalCardDrawException();
            }
        }

        // 3. 게임 상태 변경(최종 카드, 숫자, 조커, 사용자)
        GameForm gameForm = gameService.selectGame(GameForm.convert(gamerForm.getGame()));
        gameForm.setLastCardCnt(inGameForm.getDeckIds().size());
        gameForm.setLastCardRank(lastCardRank);
        gameForm.setLastJokerCnt(lastJokerCnt);
        gameForm.setLastUser(gamerForm.getUser());

        // 4. 게임 참가자 상태 변경
        // 4.1. 게임 참가자 turn 여부 변경
        // 4.1.1. 제출 카드가 달무티 혹은 Rank 보다 큰 경우는 턴 유지(최종 제출시에만 턴 변경, 이 경우, LastUser도 변경)
        if(gameForm.getLastCardRank() == CardRankEnum.DALMUTI.ordinal() || gameForm.getLastCardRank() < gameForm.getLastCardCnt()) {
            if(gamerForm.getCardTotCnt() - inGameForm.getDeckIds().size() == 0) {
                gameForm.setLastUser(updateGamerTurn(gamerForm).getUser());
            }
        } else {
            // 4.1.2. 일반 제출의 경우, 턴 변경
            updateGamerTurn(gamerForm);
        }
        gamerForm = gamerService.selectGamer(gamerForm);

        // 4.2. 게임 참가자 카드 개수 변경
        gamerForm.setJokerCnt(gamerForm.getJokerCnt() - lastJokerCnt);
        gamerForm.setCardTotCnt(gamerForm.getCardTotCnt() - inGameForm.getDeckIds().size());
        if(gamerForm.getCardTotCnt() == 0) {
            // 4.3. 게임 참가자 카드가 모두 제출된 경우, 결과 Rank 값 변경
            List<GamerForm> gamerFormList =  gamerService.selectGamerList(gamerForm);
            List<GamerForm> gamerStatusFormList = gamerService.selectGamerStatusList(gamerForm);

            gamerForm.setNxtRank(gamerFormList.size() - gamerStatusFormList.size() + 1);
            // 4.4. 게임 참가자 상태값 변경(게임진행 -> 게임종료)
            gamerForm.setStatus(GamerStatusEnum.END.ordinal());

            // 5. 최종 제출여부 점검(제출 후, 잔여 카드 개수 있는 참가자가 1명만 남은 경우)
            if(gamerForm.getNxtRank() == gamerFormList.size() - 1) {
                // 5.1. 게임 상태값 변경(게임진행 -> 게임종료)
                gameForm.setGameCode(GameCodeEnum.END.ordinal());
                // 5.2. 잔여 게임 참가자 상태 변경(게임진행 -> 게임종료)
                // 5.2. 잔여 게임 참가자 상태 변경(게임진행 -> 게임종료)
                GamerForm lastGamerForm;
                if(gamerForm.getUser().getUserId().equals(gamerStatusFormList.get(0).getUser().getUserId())) {
                    lastGamerForm = gamerStatusFormList.get(1);
                } else {
                    lastGamerForm = gamerStatusFormList.get(0);
                }
                lastGamerForm.setCardTotCnt(0);
                lastGamerForm.setJokerCnt(0);
                lastGamerForm.setNxtRank(gamerFormList.size());
                lastGamerForm.setStatus(GamerStatusEnum.END.ordinal());
                gamerService.updateGamer(lastGamerForm);
                // 5.3. 잔여 카드 삭제
                List<DeckForm> lastDeckFormList = deckService.selectDeckList(DeckForm.convert(Gamer.createGamer(lastGamerForm)));
                for(DeckForm lastDeckForm:lastDeckFormList) {
                    deckService.deleteDeck(lastDeckForm);
                }
                // 5.4. 전체 게이머 Rank 변경(Next Rank -> Rank)
                gamerForm.setRank(gamerForm.getNxtRank()!=0?gamerForm.getNxtRank():gamerForm.getRank());
                gamerForm.setNxtRank(0);
                gamerFormList =  gamerService.selectGamerList(gamerForm);
                for(GamerForm gamerRankForm:gamerFormList) {
                    if(!gamerForm.getGamerId().equals(gamerRankForm.getGamerId())) {
                        gamerRankForm.setRank(gamerRankForm.getNxtRank()!=0?gamerRankForm.getNxtRank():gamerRankForm.getRank());
                        gamerRankForm.setNxtRank(0);
                        gamerService.updateGamer(gamerRankForm);
                    }
                }
            }
        }

        gamerService.updateGamer(gamerForm);
        gameService.updateGame(gameForm);
    }

    /**
     * 게임결과를 확인한다.
     *
     * @param : inGameForm 게임 참가자 및 카드정보
     * @return :
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 2. 다음 게임 진행을 위한 게임 참가자 데이터 초기화
     * 3. 게임 상태 변경을 위한 전체 참가자 상태 점검
     * 3.1. 게임 상태 변경을 위한 전체 참가자 상태 점검
     */
    @Transactional
    public void confirmGame(InGameForm inGameForm) {
        if (logger.isDebugEnabled()) {
            logger.debug("confirmGame info: {}", inGameForm.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        // 2. 다음 게임 진행을 위한 게임 참가자 데이터 초기화
        gamerForm.setCardTotCnt(0);
        gamerForm.setJokerCnt(0);
        gamerForm.setRevolution(false);
        gamerForm.setTurn(false);
        gamerForm.setStatus(GamerStatusEnum.WAIT.ordinal());
        gamerService.updateGamer(gamerForm);

        // 3. 게임 상태 변경을 위한 전체 참가자 상태 점검
        List<GamerForm> gamerFormList =  gamerService.selectGamerList(gamerForm);
        List<GamerForm> gamerStatusFormList =  gamerService.selectGamerStatusList(gamerForm);
        if(gamerFormList.size() == gamerStatusFormList.size()) {
            // 3.1. 게임 상태 변경을 위한 전체 참가자 상태 점검
            GameForm gameForm = gameService.selectGame(GameForm.convert(gamerForm.getGame()));
            gameForm.setGameCode(GameCodeEnum.READY.ordinal());
            gameForm.setLastCardCnt(0);
            gameForm.setLastCardRank(0);
            gameForm.setLastJokerCnt(0);
            gameService.updateGame(gameForm);
        }
    }

    /**
     * 게임 상태값을 변경한다.
     *
     * @param : gameForm 게임 정보
     * @return : GameForm 게임 정보
     * 1. 게임 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 2. 게임 상태코드 정상여부 확인(전달된 상태와 저장된 상태값이 다른 경우, 게임상태가 유효하지 않음)
     * 3. 게임 상태코드 수정(현재상태 + 1 / 총 상태수)
     */
    private GameForm updateGameCode(GameForm gameFormInput) {

        // 1. 게임 기 등록여부 점검(미 등록시 Exception) 및 조회
        GameForm gameForm = gameService.selectGame(gameFormInput);

        // 2. 게임 상태코드 정상여부 확인(전달된 상태와 저장된 상태값이 다른 경우, 게임상태가 유효하지 않음)
        if(gameForm.getGameCode() != gameFormInput.getGameCode()) {
            throw new IllegalGameCodeException();
        }

        // 3. 게임 상태코드 수정(현재상태 + 1 / 총 상태수)
        gameForm.setGameCode((gameForm.getGameCode() + 1) % GameCodeEnum.values().length);
        gameService.updateGame(gameForm);

        return gameForm;
    }

    /**
     * 게임 최종 제출자(선)를 변경한다.
     *
     * @param : gameForm 게임 정보
     * @return : GameForm 게임 정보
     * 1. 게임 참가자 조회
     * 2. 게임 최종 사용자 데이터 변경
     */
    private GamerForm updateGameLast(GamerForm gamerFormInput) {

        // 1. 게임 참가자 조회
        GamerForm gamerForm = gamerService.selectGamer(gamerFormInput);

        // 2. 게임 최종 사용자 데이터 변경
        GameForm gameForm = GameForm.convert(gamerForm.getGame());
        gameForm.setLastUser(gamerForm.getUser());
        gameService.updateGame(gameForm);

        return gamerForm;
    }

    /**
     * 게임 정보(turn user)를 변경한다.
     *
     * @param : gameForm 게임 정보
     * @return : GameForm 게임 정보
     * 1. 게임 참가자 전체 조회
     * 2. 게임 참가자 순서에 따른 turn user 설정
     * 2.1. 카드확인 단계 시, 첫번째 참가자로 설정
     * 2.2. 다음 참가자로 설정(현재 turn이 마지막 참가자인 경우, 첫번째 참가자로 설정)
     * 3. 게임 참가자의 상태가 게임종료인 있는 경우, 그 다음 순번으로 turn 변경
     * 3.1. 다음 순번으로 turn 변경하려는 참가자가 현재 게임의 선일 경우, 선도 변경
     * 3.2. 다음 참가자로 turn 변경(다음 순번의 turn이 마지막 참가자인 경우, 첫번째 참가자로 설정)
     */
    private GamerForm updateGamerTurn(GamerForm gamerFormInput) {

        // 1. 게임 참가자 전체 조회
        GamerForm gamerForm = gamerService.selectGamer(gamerFormInput);
        List<GamerForm> gamerFormList =  gamerService.selectGamerList(gamerForm);

        // 2. 게임 참가자 순서에 따른 turn user 설정
        // 2.1. 카드확인 단계 시, 첫번째 참가자로 설정
        if(!gamerFormList.isEmpty() && gamerForm.getGame().getGameCode() == GameCodeEnum.READY.ordinal()) {
            gamerForm = gamerFormList.get(0);
        } else {
            int index = 0;
            while(index < gamerFormList.size()) {
                if(gamerFormList.get(index).isTurn()) {
                    gamerForm = gamerFormList.get(index);
                    gamerForm.setTurn(false);
                    gamerService.updateGamer(gamerForm);
                    break;
                }
                index += 1;
            }

            // 2.2. 다음 참가자로 설정(현재 turn이 마지막 참가자인 경우, 첫번째 참가자로 설정)
            gamerForm = gamerFormList.get(++index%(gamerFormList.size()));

            // 3. 게임 참가자의 상태가 게임종료인 있는 경우, 그 다음 순번으로 turn 변경
            if(gamerForm.getStatus() == GamerStatusEnum.END.ordinal()) {
                // 3.1. 다음 순번으로 turn 변경하려는 참가자가 현재 게임의 선일 경우, 선도 변경
                if(gamerForm.getGame().getLastUser().equals(gamerForm.getUser())) {
                    GameForm gameForm = gameService.selectGame(GameForm.convert(gamerForm.getGame()));
                    // 3.2. 다음 참가자로 turn 변경(다음 순번의 turn이 마지막 참가자인 경우, 첫번째 참가자로 설정)
                    gameForm.setLastUser(gamerFormList.get(++index%(gamerFormList.size())).getUser());
                    gameService.updateGame(gameForm);
                }
                gamerForm.setTurn(true);
                gamerService.updateGamer(gamerForm);

                gamerForm = updateGamerTurn(gamerForm);
            }
        }

        if(gamerForm.getStatus() == GamerStatusEnum.PLAY.ordinal()) {
            gamerForm.setTurn(true);
            gamerService.updateGamer(gamerForm);
        }

        return gamerForm;
    }

    /**
     * 게임 참가자의 상태값을 변경한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return : GamerForm 게임 참가자 정보
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     * 2. 게임 참가자 상태 정상여부 확인(전달된 상태와 저장된 상태값이 다른 경우, 게임상태가 유효하지 않음)
     * 3. 게임 참가자 상태코드 수정(현재상태 + 1 / 총 상태수)
     */
    private GamerForm updateGamerStatus(GamerForm gamerFormInput) {

        if (logger.isDebugEnabled()) {
            logger.debug("updateGamerStatus info: {}", gamerFormInput.toString());
        }

        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        GamerForm gamerForm = gamerService.selectGamer(gamerFormInput);

        // 2. 게임 참가자 상태 정상여부 확인(전달된 상태와 저장된 상태값이 다른 경우, 게임상태가 유효하지 않음)
        // 화면에서 객체를 받지 않아 불필요해졌음.
        // if(gamerForm.getStatus() != gamerFormInput.getStatus()) {
        //     throw new IllegalGamerStatusException();
        // }

        // 3. 게임 참가자 상태코드 수정(현재상태 + 1 / 총 상태수)
        gamerForm.setStatus((gamerForm.getStatus() + 1) % GamerStatusEnum.values().length);
        gamerService.updateGamer(gamerForm);

        return gamerForm;
    }

    /**
     * 게임참여 validation
     *
     * @param : EngageGamerForm 게임 정보
     * @return :
     * 1. 대기실 게이머 생성
     */
    @Transactional
    public GamerForm validEngageGame(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("validEngageGame info: {}", inGameForm.toString());
        }
        GamerForm gamerForm = new GamerForm();
        GameForm gameForm = gameService.selectGame(GameForm.convert(inGameForm.getGameId()));

        //이미 게이머로 참여하고 있는 사람은 통과
        gamerForm.setGame(Game.createGame(gameForm));

        // 1. 게이머 조회()
        List<GamerForm> gamerFormList = gamerService.selectGamerList(gamerForm);

        // 2. 게이머 체크(기존에 userName이 같으면 return)
        for(int i = 0; i < gamerFormList.size(); i++) {
            if(inGameForm.getUserName().equals(gamerFormList.get(i).getUser().getUserName())) {
                return gamerFormList.get(i);
            }
        }

        //참여하면 게이머가 8명 초과임
        if(gamerFormList.size() >= 8) {
            throw new IllegalGamerCountException();
        }

        //게임이 이미 시작됨
        if(GameCodeEnum.WAIT.ordinal() != gameForm.getGameCode()) {
            throw new IllegalGameAlreadyStartException();
        }

        return new GamerForm();
    }


    /**
     * 대기실 게임참여 게이머생성
     *
     * @param : EngageGamerForm 게임 정보
     * @return :
     * 1. 대기실 게이머 생성
     */
    @Transactional
    public GamerForm engageGame(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("engageGame info: {}", inGameForm.toString());
        }

        GamerForm gamerForm = new GamerForm();
        GameForm gameForm = gameService.selectGame(GameForm.convert(inGameForm.getGameId()));

        //게임이 이미 시작됨
        if(GameCodeEnum.WAIT.ordinal() != gameForm.getGameCode()) {
            throw new IllegalGameAlreadyStartException();
        }

        
        
        gamerForm.setGame(Game.createGame(gameForm));
        gamerForm.setUser(User.createUser(userManager.getUser(UserForm.convert(inGameForm.getUserId()))));

        // 1. 게이머 랭크 조회()
        List<GamerForm> gamerFormList = gamerService.selectGamerList(gamerForm);

        //참여하면 게이머가 8명 초과임
        if(gamerFormList.size() >= 8) {
            throw new IllegalGamerCountException();
        }

        // 2. 게이머 체크(기존에 userName이 같으면 retrun)
        for(int i = 0; i < gamerFormList.size(); i++) {
            if(inGameForm.getUserId().equals(gamerFormList.get(i).getUser().getUserId())) {
                return gamerService.selectGamer(gamerFormList.get(i));
            }
        }

        if(gamerFormList.size() == 0) {
            gamerForm.setRank(21);
        } else {
            gamerForm.setRank(gamerFormList.get(gamerFormList.size()-1).getRank() + 1);
        }
        
        // 2. 대기실 게이머 생성
        String gamerId = gamerService.createGamer(gamerForm);

        // 3. 게임 플레이어 수 update
        gameForm.setTotGamerCnt(gamerFormList.size()+1);
        gameService.updateGame(gameForm);

        gamerForm.setGamerId(gamerId);

        return gamerService.selectGamer(gamerForm);
    }
    /**
     * 대기실 게이머 조회
     *
     * @param : GameForm 게임 정보
     * @return :
     * 1. 대기실 게이머 조회
     */
    @Transactional
    public List<GamerForm> getWaitRoomGamerList(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("getWaitRoomGamerList info: {}", inGameForm.toString());
        }

        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        return gamerService.selectGamerList(gamerForm);
    }

    /**
     * 선을 정하기 위해 카드를 선택하기 위한 단계로 넘어감
     *
     * @param : gamerForm 게임 참가자 정보
     * @return :
     * 1. 전달받은 참가자의 게임정보 조회
     * 2. 게임코드 변경 0(대기중) -> 1(게임준비)
     */
    @Transactional
    public void pickCardStart(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("pickCard info: {}", inGameForm.toString());
        }

        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        GameForm gameForm = gameService.selectGame(GameForm.convert(gamerForm.getGame()));

        if(gameForm.getGameCode() != GameCodeEnum.WAIT.ordinal()) {
            throw new IllegalGameCodeException();
        }

        gameForm.setGameCode(GameCodeEnum.READY.ordinal());

        gameService.updateGame(gameForm);

    }

    /**
     * 게이머 덱 조회
     *
     * @param : GamerForm 게이머 정보
     * @return :
     * 1. 게이머 덱 조회
     */
    @Transactional
    public List<DeckForm> getGamerDeckList(InGameForm inGameForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("getGamerDeckList info: {}", inGameForm.toString());
        }

        GamerForm gamerForm = gamerService.selectGamer(GamerForm.convert(inGameForm.getGamerId()));

        DeckForm deckForm = new DeckForm();

        deckForm.setGamer(Gamer.createGamer(gamerForm));

        return deckService.selectDeckList(deckForm);
    }

    /**
     * 카드생성
     *
     */

    private void createCard() { 

        logger.debug("createCard ");

        int k = 0;
        // 1. 분배대상 카드 생성
        for(int i = 1; i <= 12 ;i++) {
            for(int j = 1; j<=i;j++) {
                CardForm cardForm = new CardForm();
                k++;
                if( k < 10) {
                    cardForm.setCardId("card00000" + String.valueOf(k));
                } else {
                    cardForm.setCardId("card0000" + String.valueOf(k));
                }

                cardForm.setRank(i);

                logger.debug("Card Info {}", cardForm.toString());

                cardService.saveCard(cardForm);
            }
        }
        CardForm cardForm = new CardForm();

        cardForm.setCardId("card000079");
        cardForm.setRank(CardRankEnum.JESTER.ordinal());

        logger.debug("Card Info {}", cardForm.toString());

        cardService.saveCard(cardForm);

        cardForm.setCardId("card000080");
        cardForm.setRank(CardRankEnum.JESTER.ordinal());
        
        logger.debug("Card Info {}", cardForm.toString());

        cardService.saveCard(cardForm);
    }


}
