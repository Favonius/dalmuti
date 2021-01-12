package kbank.sandbox.dalmuti.game.domain;

import kbank.sandbox.dalmuti.game.dto.GamerForm;
import kbank.sandbox.dalmuti.user.domain.User;
import lombok.*;

import javax.persistence.*;

/**
 * <pre>
 * 파 일 명 : Gamer.java
 * 설    명 : Gamer Entity
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Entity
@Table(name = "gamer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode()
@ToString()
public class Gamer {

    @Id
    @Column(length = 10, unique = true, nullable = false)
    private String gamerId;

    @ManyToOne
    @JoinColumn(name = "game")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private int nxtRank;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private int cardTotCnt;

    @Column(nullable = false)
    private int jokerCnt;

    @Column(nullable = false)
    private boolean isTurn;

    @Column(nullable = false)
    private boolean isRevolution;

    private Gamer(GamerForm gamerForm) {
        this(gamerForm.getGamerId(), gamerForm.getGame(), gamerForm.getUser(), gamerForm.getRank(), gamerForm.getNxtRank(), gamerForm.getStatus(), gamerForm.getCardTotCnt(), gamerForm.getJokerCnt(), gamerForm.isTurn(), gamerForm.isRevolution());
    }

    /**
     * 신규 게임 참가자를 생성한다.
     *
     * @param : gamerForm 게임 참가자 정보
     * @return :
     */
    public static Gamer createGamer(GamerForm gamerForm) {
        return new Gamer(gamerForm);
    }
}
