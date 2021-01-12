package kbank.sandbox.dalmuti.exception;

import org.json.simple.JSONObject;

/**
 * <pre>
 * 파 일 명 : ErrorResponse.java
 * 설    명 : 에러 응답 데이터 구성
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
public class ErrorResponse {

    /**
     * 에러 발생시 응답 데이터 구성
     *
     * @param : errorCode 에러코드, errorMessage 에러메시지
     * @return : JSONObject 에러코드/메시지 포함된 json object
     */
    public static JSONObject JsonErrorResponse(int errorCode, String errorMessage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", errorCode);
        jsonObject.put("message", errorMessage);
        return jsonObject;
    }

}
