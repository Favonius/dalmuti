package kbank.sandbox.dalmuti.game.domain;

import kbank.sandbox.dalmuti.game.dto.GameForm;
import kbank.sandbox.dalmuti.user.domain.User;
import lombok.*;

import javax.persistence.*;

/**
 * <pre>
 * 파 일 명 : Game.java
 * 설    명 : Game Entity
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Entity
@Table(name = "game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode()
@ToString()
public class Game {

    @Id
    @Column(length = 6, unique = true, nullable = false)
    private String gameId;

    @Column(nullable = false)
    private int gameCode;

    @ManyToOne
    @JoinColumn(name = "last_user")
    private User lastUser;

    @Column(nullable = false)
    private int lastCardRank;

    @Column(nullable = false)
    private int lastCardCnt;

    @Column(nullable = false)
    private int lastJokerCnt;

    @Column(nullable = false)
    private int totGamerCnt;

    private Game(GameForm gameForm) {
        this(gameForm.getGameId(), gameForm.getGameCode(), gameForm.getLastUser(), gameForm.getLastCardRank(), gameForm.getLastCardCnt(), gameForm.getLastJokerCnt(), gameForm.getTotGamerCnt());
    }

    /**
     * 신규 게임을 생성한다.
     *
     * @param : gameForm 게임 정보
     * @return :
     */
    public static Game createGame(GameForm gameForm) {
        return new Game(gameForm);
    }

}