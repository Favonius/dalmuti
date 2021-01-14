package kbank.sandbox.dalmuti.user.service;

import kbank.sandbox.dalmuti.user.domain.User;
import kbank.sandbox.dalmuti.user.dto.UserForm;
import kbank.sandbox.dalmuti.user.exception.IllegalUserIdException;
import kbank.sandbox.dalmuti.user.exception.IllegalUserNameException;
import kbank.sandbox.dalmuti.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * <pre>
 * 파 일 명 : UserService.java
 * 설    명 : 사용자 서비스
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    /**
     * 로그인 처리를 수행한다.
     * 
     * @param : userForm 사용자 정보
     * @return :
     * 1. 사용자 ID 생성
     * 2. 사용자 등록
     */
    @Transactional
    public UserForm createUser(UserForm userForm) {
        
        UserForm out = new UserForm();
        // 1. 사용자 ID 생성
        String userId = generateUserId();

        if(logger.isDebugEnabled()) {
            logger.debug("사용자 ID : {}", userId);
        }

        // 2. 사용자 등록
        userForm.setUserId(userId);
        userRepository.save(User.createUser(userForm));

        out.setUserId(userId);
        out.setUserName(userForm.getUserName());

        return out;
    }

    /**
     * 정상 사용자 여부를 검증한다.
     *
     * @param : userForm 사용자 정보
     * @return : UserForm 사용자 정보
     * 1. 사용자 기 등록여부 점검(미 등록시 Exception) 및 조회
     */
    @Transactional
    public UserForm selectUser(UserForm userForm) {
        // 1. 사용자 기 등록여부 점검(미 등록시 Exception) 및 조회
        return UserForm.convert(userRepository.findById(userForm.getUserId()).orElseThrow(IllegalUserIdException::new));
    }

    /**
     * 정상 사용자 여부를 검증한다.
     *
     * @param : userForm 사용자 정보
     * @return : UserForm 사용자 정보
     * 1. 사용자 기 등록여부 점검 및 조회
     */
    @Transactional
    public UserForm selectUserByUserName(UserForm userForm) {
        // 1. 사용자 기 등록여부 점검 및 조회
        // 1. 사용자 기 등록여부 점검 및 조회
        UserForm userNameForm;
        try {
            userNameForm = UserForm.convert(userRepository.findByUserName(userForm.getUserName()).orElseThrow(IllegalUserNameException::new));
        } catch(IllegalUserNameException e) {
            userNameForm = null;
        }
        return userNameForm;
    }

    /**
     * 사용자 ID를 생성한다.
     * 
     * @param :
     * @return : userId 사용자 ID
     * 1. 사용자 ID 생성(숫자 : 10자리)
     * 2. 사용자 기 등록여부 점검(기 등록시 재귀호출)
     */
    private String generateUserId() {
        // 1. 사용자 ID 생성(숫자 : 10자리)
        String userId = RandomStringUtils.randomNumeric(10);

        // 2. 사용자 기 등록여부 점검(기 등록시 재귀호출)
        if(userRepository.existsByUserId(userId)) {
            userId = generateUserId();
        }

        return userId;
    }
}
