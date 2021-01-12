package kbank.sandbox.dalmuti.game.domain;

import kbank.sandbox.dalmuti.game.dto.CardForm;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <pre>
 * 파 일 명 : Card.java
 * 설    명 : Card Entity
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Entity
@Table(name = "card")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode()
@ToString()
public class Card {

    @Id
    @Column(length = 10, unique = true, nullable = false)
    private String cardId;

    @Column(nullable = false)
    private int rank;

    private Card(CardForm cardForm) {
        this(cardForm.getCardId(), cardForm.getRank());
    }

    /**
     * 신규 카드를 생성한다.
     *
     * @param : cardForm 카드 정보
     * @return :
     */
    public static Card createCard(CardForm cardForm) {
        return new Card(cardForm);
    }
}
