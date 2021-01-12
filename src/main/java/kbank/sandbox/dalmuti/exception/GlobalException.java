package kbank.sandbox.dalmuti.exception;

import kbank.sandbox.dalmuti.game.exception.*;
import kbank.sandbox.dalmuti.user.exception.IllegalUserIdException;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <pre>
 * 파 일 명 : GlobalException.java
 * 설    명 : Exception 전역 처리
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
@ControllerAdvice
@ResponseBody
public class GlobalException {

    @ExceptionHandler(IllegalGameIdException.class)
    public JSONObject illegalGameIdException(){
        return ErrorResponse.JsonErrorResponse(400, "게임 ID가 유효하지 않습니다.");
    }

    @ExceptionHandler(IllegalGameCodeException.class)
    public JSONObject illegalGameCodeException(){
        return ErrorResponse.JsonErrorResponse(400, "게임상태가 유효하지 않습니다.");
    }

    @ExceptionHandler(IllegalUserIdException.class)
    public JSONObject illegalUserIdException(){
        return ErrorResponse.JsonErrorResponse(400, "해당 사용자가 존재하지 않습니다.");
    }

    @ExceptionHandler(IllegalGamerCountException.class)
    public JSONObject illegalGamerCountException(){
        return ErrorResponse.JsonErrorResponse(400, "플레이어 숫자(5~8명)가 맞지 않습니다.");
    }

    @ExceptionHandler(IllegalGamerRankException.class)
    public JSONObject illegalGamerRankException(){
        return ErrorResponse.JsonErrorResponse(400, "플레이어들의 계급이 유효하지 않습니다.");
    }

    @ExceptionHandler(IllegalCardIdException.class)
    public JSONObject illegalCardIdException(){
        return ErrorResponse.JsonErrorResponse(400, "카드가 유효하지 않습니다.");
    }

    @ExceptionHandler(IllegalCardDrawException.class)
    public JSONObject illegalCardDrawException(){
        return ErrorResponse.JsonErrorResponse(400, "선택한 카드가 유효하지 않습니다. 다른 카드를 선택해주세요.");
    }

    @ExceptionHandler(IllegalGamerRevolutionException.class)
    public JSONObject illegalGamerRevolutionException(){
        return ErrorResponse.JsonErrorResponse(400, "혁명이 실패하였습니다.");
    }

    @ExceptionHandler(IllegalGamerTaxException.class)
    public JSONObject illegalGamerTaxException(){
        return ErrorResponse.JsonErrorResponse(400, "세금을 처리 할 수 없습니다.");
    }
    
    @ExceptionHandler(IllegalDeckIdException.class)
    public JSONObject illegalDeckIdException(){
        return ErrorResponse.JsonErrorResponse(400, "선택한 카드가 유효하지 않습니다.");
    }
    
    @ExceptionHandler(IllegalGamerTurnException.class)
    public JSONObject illegalGamerTurnException(){
        return ErrorResponse.JsonErrorResponse(400, "플레이어의 차례가 아닙니다.");
    }

    @ExceptionHandler(IllegalGamerLastUserException.class)
    public JSONObject illegalGamerLastUserException(){
        return ErrorResponse.JsonErrorResponse(400, "플레이어는 선입니다.");
    }
}
