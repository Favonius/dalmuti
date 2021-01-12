package kbank.sandbox.dalmuti.game.domain;

import kbank.sandbox.dalmuti.game.dto.DeckForm;
import lombok.*;

import javax.persistence.*;

/**
 * <pre>
 * 파 일 명 : Deck.java
 * 설    명 : Deck Entity
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Entity
@Table(name = "deck")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode()
@ToString()
public class Deck {

    @Id
    @Column(length = 10, unique = true, nullable = false)
    private String deckId;

    @ManyToOne
    @JoinColumn(name = "gamer")
    private Gamer gamer;

    @ManyToOne
    @JoinColumn(name = "card")
    private Card card;

    @Column(nullable = false)
    private boolean isTax;

    private Deck(DeckForm deckForm) {
        this(deckForm.getDeckId(), deckForm.getGamer(), deckForm.getCard(), deckForm.isTax());
    }

    /**
     * 신규 덱을 생성한다.
     *
     * @param : deckForm 덱 정보
     * @return :
     */
    public static Deck createDeck(DeckForm deckForm) {
        return new Deck(deckForm);
    }
}
