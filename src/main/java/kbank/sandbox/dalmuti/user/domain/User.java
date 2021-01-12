package kbank.sandbox.dalmuti.user.domain;

import kbank.sandbox.dalmuti.user.dto.UserForm;
import lombok.*;

import javax.persistence.*;

/**
 * <pre>
 * 파 일 명 : User.java
 * 설    명 : User Entity
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode()
@ToString()
public class User {

    @Id
    @Column(length = 10, unique = true, nullable = false)
    private String userId;

    @Column(length = 50, nullable = false)
    private String userName;

    public User(UserForm userForm) {
        this(userForm.getUserId(), userForm.getUserName());
    }

    /**
     * 신규 사용자를 생성한다.
     *
     * @param : userForm 사용자 정보
     * @return :
     */
    public static User createUser(UserForm userForm) {
        return new User(userForm);
    }
}