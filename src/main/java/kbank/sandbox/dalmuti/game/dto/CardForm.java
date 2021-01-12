package kbank.sandbox.dalmuti.game.dto;

import kbank.sandbox.dalmuti.game.domain.Card;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 파 일 명 : CardForm.java
 * 설    명 : Card Entity DTO
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
public class CardForm {

    @Size(max=10, message="카드 ID는 10자리를 초과할 수 없습니다.")
    private String cardId;

    @NotEmpty(message = "계급은 필수 입니다.")
    @Min(1)
    @Max(13)
    private int rank;

    /**
     * domain 객체 기반 dto 객체를 생성한다.
     *
     * @param : card Card entity
     * @return : CardForm
     */
    public static CardForm convert(Card card) {
        return new CardForm(card.getCardId(), card.getRank());
    }

    /**
     * domain 객체 기반 dto 객체 리스트를 생성한다.
     *
     * @param : cardList Card entity List
     * @return :
     */
    public static List<CardForm> convert(List<Card> cardList) {
        List<CardForm> cardFormList = new ArrayList<>();

        for(Card card:cardList) {
            cardFormList.add(CardForm.convert(card));
        }

        return cardFormList;
    }
}
