package kbank.sandbox.dalmuti.user.dto;

import kbank.sandbox.dalmuti.user.domain.User;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * <pre>
 * 파 일 명 : UserForm.java
 * 설    명 : User Entity DTO
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
public class UserForm {

    @Size(max=10, message="사용자 ID는 10자리를 초과할 수 없습니다.")
    private String userId;

    @NotEmpty(message = "사용자명은 필수 입니다.")
    @Size(max=50, message="사용자명은 50자리를 초과할 수 없습니다.")
    private String userName;

    /**
     * userId 기반 dto 객체를 생성한다.
     *
     * @param : userId UserID
     * @return :
     */
    public static UserForm convert(String userId) {
        UserForm userForm = new UserForm();
        userForm.setUserId(userId);
        return userForm;
    }

    /**
     * domain 객체 기반 dto 객체를 생성한다.
     *
     * @param : user User entity
     * @return :
     */
    public static UserForm convert(User user) {
        return new UserForm(user.getUserId(), user.getUserName());
    }

}
