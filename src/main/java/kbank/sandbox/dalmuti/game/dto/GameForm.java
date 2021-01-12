package kbank.sandbox.dalmuti.game.dto;

import kbank.sandbox.dalmuti.game.domain.Game;
import kbank.sandbox.dalmuti.user.domain.User;
import lombok.*;

import javax.validation.constraints.*;

/**
 * <pre>
 * 파 일 명 : GameForm.java
 * 설    명 : Game Entity DTO
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class GameForm {

    @Size(max=6, message="게임 ID는 6자리를 초과할 수 없습니다.")
    private String gameId;

    @NotNull(message = "잘못된 상태값입니다.(0:대기실, 1:게임준비, 2:카드확인, 3:세금징수, 4:게임진행, 5:게임종료)")
    @Min(0)
    @Max(5)
    private int gameCode;

    private User lastUser;

    @NotNull(message = "카드의 계급(1~12, joker)이 맞지 않습니다.")
    @Min(0)
    @Max(13)
    private int lastCardRank;

    @NotNull(message = "카드의 수가 맞지 않습니다.")
    @Min(0)
    @Max(12)
    private int lastCardCnt;

    @NotNull(message = "카드의 수가 맞지 않습니다.")
    @Min(0)
    @Max(2)
    private int lastJokerCnt;

    @NotNull(message = "참가 플레이어 수가 맞지 않습니다.")
    @Min(0)
    @Max(8)
    private int totGamerCnt;

    /**
     * gameId 기반 dto 객체를 생성한다.
     *
     * @param : gameId GameID
     * @return :
     */
    public static GameForm convert(String gameId) {
        GameForm gameForm = new GameForm();
        gameForm.setGameId(gameId);
        return gameForm;
    }

    /**
     * domain 객체 기반 dto 객체를 생성한다.
     *
     * @param : game Game entity
     * @return :
     */
    public static GameForm convert(Game game) {
        return new GameForm(game.getGameId(), game.getGameCode(), game.getLastUser(), game.getLastCardRank(), game.getLastCardCnt(), game.getLastJokerCnt(), game.getTotGamerCnt());
    }
}
