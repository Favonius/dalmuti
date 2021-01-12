package kbank.sandbox.dalmuti.message.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <pre>
 * 파 일 명 : MessageForm.java
 * 설    명 : Message DTO
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
public class MessageForm {

    @Size(max=10, message="사용자 ID는 10자리를 초과할 수 없습니다.")
    private String userId;

    @Size(max=6, message="게임 ID는 6자리를 초과할 수 없습니다.")
    private String gameId;

    @NotNull(message = "잘못된 메시지 코드입니다.(0:새로고침, 1:레볼루션)")
    @Min(0)
    @Max(1)
    private int messageCode;

    @Size(max=50, message="메시지는 50자리를 초과할 수 없습니다.")
    private String message;

}
