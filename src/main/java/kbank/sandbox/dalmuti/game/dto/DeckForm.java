package kbank.sandbox.dalmuti.game.dto;

import kbank.sandbox.dalmuti.game.domain.Card;
import kbank.sandbox.dalmuti.game.domain.Deck;
import kbank.sandbox.dalmuti.game.domain.Gamer;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 파 일 명 : DeckForm.java
 * 설    명 : Deck Entity DTO
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
public class DeckForm {

    @Size(max=10, message="덱 ID는 10자리를 초과할 수 없습니다.")
    private String deckId;

    private Gamer gamer;

    private Card card;

    private boolean isTax;

    /**
     * deckId 기반 dto 객체를 생성한다.
     *
     * @param : deckId DeckID
     * @return :
     */
    public static DeckForm convert(String deckId) {
        DeckForm deckForm = new DeckForm();
        deckForm.setDeckId(deckId);
        return deckForm;
    }

    /**
     * gamer 기반 dto 객체를 생성한다.
     *
     * @param : gamer Gamer
     * @return :
     */
    public static DeckForm convert(Gamer gamer) {
        DeckForm deckForm = new DeckForm();
        deckForm.setGamer(gamer);
        return deckForm;
    }

    /**
     * domain 객체 기반 dto 객체를 생성한다.
     *
     * @param : gamer Gamer entity
     * @return :
     */
    public static DeckForm convert(Deck deck) {
        return new DeckForm(deck.getDeckId(), deck.getGamer(), deck.getCard(), deck.isTax());
    }

    /**
     * domain 객체 기반 dto 객체 리스트를 생성한다.
     *
     * @param : deckList Deck entity List
     * @return :
     */
    public static List<DeckForm> convert(List<Deck> deckList) {
        List<DeckForm> deckFormList = new ArrayList<>();

        for(Deck deck:deckList) {
            deckFormList.add(DeckForm.convert(deck));
        }

        return deckFormList;
    }
}
