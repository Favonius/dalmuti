package kbank.sandbox.dalmuti.game.dto;

import kbank.sandbox.dalmuti.game.domain.Game;
import kbank.sandbox.dalmuti.game.domain.Gamer;
import kbank.sandbox.dalmuti.user.domain.User;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 파 일 명 : GamerForm.java
 * 설    명 : Gamer Entity DTO
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
public class GamerForm  implements Comparable<GamerForm> {

    @Size(max=10, message="게임 참가자 ID는 10자리를 초과할 수 없습니다.")
    private String gamerId;

    private Game game;

    private User user;

    @NotNull(message = "계급(1~12)이 맞지 않습니다.")
    @Min(0)
    @Max(28)
    private int rank;

    @NotNull(message = "계급(1~12)이 맞지 않습니다.")
    @Min(0)
    @Max(13)
    private int nxtRank;

    @NotNull(message = "잘못된 상태값입니다.(0:카드대기, 1:세금징수, 2:게임진행, 3:게임종료)")
    @Min(0)
    @Max(3)
    private int status;

    @NotNull(message = "카드의 수가 맞지 않습니다.")
    @Min(0)
    @Max(80)
    private int cardTotCnt;

    @NotNull(message = "카드의 수가 맞지 않습니다.")
    @Min(0)
    @Max(2)
    private int jokerCnt;

    private boolean isTurn;

    private boolean isRevolution;

    /**
     * gamerId 기반 dto 객체를 생성한다.
     *
     * @param : gamerId GamerID
     * @return :
     */
    public static GamerForm convert(String gamerId) {
        GamerForm gamerForm = new GamerForm();
        gamerForm.setGamerId(gamerId);
        return gamerForm;
    }

    /**
     * domain 객체 기반 dto 객체를 생성한다.
     *
     * @param : gamer Gamer entity
     * @return :
     */
    public static GamerForm convert(Gamer gamer) {
        return new GamerForm(gamer.getGamerId(), gamer.getGame(), gamer.getUser(), gamer.getRank(), gamer.getNxtRank(), gamer.getStatus(), gamer.getCardTotCnt(), gamer.getJokerCnt(), gamer.isTurn(), gamer.isRevolution());
    }

    /**
     * domain 객체 기반 dto 객체 리스트를 생성한다.
     *
     * @param : gamerList Gamer entity List
     * @return :
     */
    public static List<GamerForm> convert(List<Gamer> gamerList) {
        List<GamerForm> gamerFormList = new ArrayList<>();

        for(Gamer gamer:gamerList) {
            gamerFormList.add(GamerForm.convert(gamer));
        }

        return gamerFormList;
    }

    @Override
    public int compareTo(GamerForm gamerForm) {
        if(this.rank > gamerForm.rank) {
            return 1;
        } else if(this.rank < gamerForm.rank) {
            return -1;
        } else {
            return 0;
        }
    }
}
