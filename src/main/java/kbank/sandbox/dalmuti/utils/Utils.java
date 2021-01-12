package kbank.sandbox.dalmuti.utils;



/**
 * <pre>
 * 파 일 명 : Utils.java
 * 설    명 : Utils
 * 작 성 일 : 2020.12.01
 * 버    전 : 1.0
 * 변경사항 :
 * Copyright 2020 by K BANK. All rights reserved.
 * </pre>
 */
public class Utils {


    /**
     * Lpad
     *
     * @param : String src, int padSize, String padStr
     * @return :
     * 1. 입력받은 문자열의 갯수만큼 문자열을 패딩해줌
     */
    public static String lpad(String src, int padSize, String padStr) {
        String result = src;
        int length = padSize - src.length();
        for(int i=0;i<length ;i++) {
            result = padStr + result;
        }
        return result;
    }


}
