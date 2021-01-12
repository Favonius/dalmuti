package kbank.sandbox.dalmuti.user.business;

import kbank.sandbox.dalmuti.user.dto.UserForm;
import kbank.sandbox.dalmuti.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * <pre>
 * 파 일 명 : UserManager.java
 * 설    명 : 사용자 관리
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    private final UserService userService;

    /**
     * 로그인 처리를 수행한다.
     *
     * @param : userForm 사용자 정보
     * @return :
     * 1. 사용자 등록
     */
    @Transactional
    public UserForm loginUser(UserForm userForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("login user info: {}", userForm.toString());
        }

        // 1. 사용자 등록
        return userService.createUser(userForm);
    }

    /**
     * 정상 사용자 여부를 검증한다.
     *
     * @param : userForm 사용자 정보
     * @return :
     * 1. 사용자 기 등록여부 점검(미 등록시 Exception) 및 조회
     */
    @Transactional
    public UserForm getUser(UserForm userForm) {
        if(logger.isDebugEnabled()) {
            logger.debug("select user info: {}", userForm.toString());
        }

        // 1. 사용자 기 등록여부 점검(미 등록시 Exception) 및 조회
        return userService.selectUser(userForm);
    }
}
