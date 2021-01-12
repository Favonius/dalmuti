package kbank.sandbox.dalmuti.game.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * <pre>
 * 파 일 명 : InGameForm.java
 * 설    명 : 게임 View 의 Game 구성 DTO
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
public class InGameForm {

    private String userId;

    private String gameId;

    private String gamerId;

    @Size(min=1, max=14, message="선택된 카드가 없거나, 너무 많습니다.")
    private List<String> deckIds;

}
