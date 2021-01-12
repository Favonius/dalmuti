package kbank.sandbox.dalmuti.game.service;

import kbank.sandbox.dalmuti.game.domain.Gamer;
import kbank.sandbox.dalmuti.game.dto.GamerForm;
import kbank.sandbox.dalmuti.game.exception.IllegalGamerIdException;
import kbank.sandbox.dalmuti.game.repository.GamerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * <pre>
 * 파 일 명 : GamerService.java
 * 설    명 : 게임 참가자 서비스=
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class GamerService {

    private static final Logger logger = LoggerFactory.getLogger(GamerService.class);

    private final GamerRepository gamerRepository;

    /**
     * 게임 참가자 정보를 입력한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return :
     * 1. 게임 참가자 ID 생성(숫자 + 영문 : 10자리)
     * 2. 게임 참가자 생성
     */
    @Transactional
    public String createGamer(GamerForm gamerForm) {
        
        // 1. 게임 참가자 ID 생성(숫자 + 영문 : 10자리)
        String gamerId = generateGamerId();

        if(logger.isDebugEnabled()) {
            logger.debug("게임 참가자 ID : {}", gamerId);
        }

        // 2. 게임 참가자 생성
        gamerForm.setGamerId(gamerId);
        gamerRepository.save(Gamer.createGamer(gamerForm));

        return gamerId;
    }

    /**
     * 해당 게임의 게임 참가자 정보를 조회한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return : GamerForm 게임 참가자 정보
     * 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
     */
    @Transactional
    public GamerForm selectGamer(GamerForm gamerForm) {
        // 1. 게임 참가자 기 등록여부 점검(미 등록시 Exception) 및 조회
        return GamerForm.convert(gamerRepository.findById(gamerForm.getGamerId()).orElseThrow(IllegalGamerIdException::new));
    }

    /**
     * 해당 게임의 게임 참가자 전체 정보를 조회한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return : List<GamerForm> 기준 대상 게임 참가자 list
     * 1. 게임 기준으로 대상 게임 참가자 list 조회
     */
    @Transactional
    public List<GamerForm> selectGamerList(GamerForm gamerForm) {
        // 1. 게임 기준으로 대상 게임 참가자 list 조회
        return GamerForm.convert(gamerRepository.findByGame(gamerForm.getGame(), sortAscByStr("rank")));
    }

    /**
     * 해당 게임의 게임 참가자 중 해당 계급(Rank) 정보를 조회한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return : List<GamerForm> 기준 대상 게임 참가자 list
     * 1. 게임 기준으로 대상 게임, 계급 list 조회
     */
    @Transactional
    public List<GamerForm> selectGamerRankList(GamerForm gamerForm) {
        // 1. 게임 기준으로 대상 게임 참가자 list 조회
        return GamerForm.convert(gamerRepository.findByGameAndRank(gamerForm.getGame(), gamerForm.getRank(), sortAscByStr("rank")));
    }

    /**
     * 해당 게임의 게임 참가자 중 다음 계급(Next Rank) 정보를 조회한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return : List<GamerForm> 기준 대상 게임 참가자 list
     * 1. 게임 기준으로 대상 게임, 다음 계급 list 조회
     */
    @Transactional
    public List<GamerForm> selectGamerNxtRankList(GamerForm gamerForm) {
        // 1. 게임 기준으로 대상 게임 참가자 list 조회
        return GamerForm.convert(gamerRepository.findByGameAndNxtRank(gamerForm.getGame(), gamerForm.getNxtRank(), sortAscByStr("rank")));
    }

    /**
     * 해당 게임의 게임 참가자 중 해당 상태 정보를 조회한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return : List<GamerForm> 기준 대상 게임 참가자 list
     * 1. 게임 기준으로 대상 게임, 상태 list 조회
     */
    @Transactional
    public List<GamerForm> selectGamerStatusList(GamerForm gamerForm) {
        // 1. 게임 기준으로 대상 게임 참가자 list 조회
        return GamerForm.convert(gamerRepository.findByGameAndStatus(gamerForm.getGame(), gamerForm.getStatus(), sortAscByStr("rank")));
    }

    /**
     * 해당 게임의 게임 참가자 정보를 수정한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return :
     * 1. 게임 참가자 정보를 수정
     */
    @Transactional
    public void updateGamer(GamerForm gamerForm) {
        // 1. 게임 참가자 정보를 수정
        gamerRepository.save(Gamer.createGamer(gamerForm));
    }

    /**
     * 게임 참가자 ID를 생성한다.
     *
     * @param :
     * @return : gamerId 게이머 ID
     * 1. 게임 참가자 ID 생성(숫자 + 영문 : 10자리)
     * 2. 게임 참가자 ID 기 등록여부 점검(기 등록시 재귀호출)
     */
    private String generateGamerId() {
        // 1. 게임 ID 생성(숫자 + 영문 : 10자리)
        String gamerId = RandomStringUtils.randomAlphanumeric(10).toUpperCase();

        // 2. 게임 ID 기 등록여부 점검(기 등록시 재귀호출)
        if(gamerRepository.existsByGamerId(gamerId)) {
            gamerId = generateGamerId();
        }

        return gamerId;
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
