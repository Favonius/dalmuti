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

    @Size(max=10, message="사용자 ID는 10자리를 초과할 수 없습니다.")
    private String userId;

    @Size(max=50, message="사용자명은 50자리를 초과할 수 없습니다.")
    private String userName;

    @Size(max=6, message="게임 ID는 6자리를 초과할 수 없습니다.")
    private String gameId;

    @Size(max=10, message="게임 참가자 ID는 10자리를 초과할 수 없습니다.")
    private String gamerId;

    @Size(min=1, max=14, message="선택된 카드가 없거나, 너무 많습니다.")
    private List<String> deckIds;

}
