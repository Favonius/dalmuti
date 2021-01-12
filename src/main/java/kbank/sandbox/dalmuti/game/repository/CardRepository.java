package kbank.sandbox.dalmuti.game.repository;

import kbank.sandbox.dalmuti.game.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * 파 일 명 : CardRepository.java
 * 설    명 : 카드 Entity 처리 Bean
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
public interface CardRepository extends JpaRepository<Card, String> {
}
