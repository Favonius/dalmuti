package kbank.sandbox.dalmuti.user.repository;

import kbank.sandbox.dalmuti.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * 파 일 명 : UserReporitory.java
 * 설    명 : 사용자 Entity 처리 Bean
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 사용자 ID 조회
     *
     * @param : username 사용자 ID
     * @return : boolean 기등록여부 True/False
     */
    boolean existsByUserId(String userId);
}
