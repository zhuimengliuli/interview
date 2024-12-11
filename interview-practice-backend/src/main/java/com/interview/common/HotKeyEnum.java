package com.interview.common;

/**
 * @author hjc
 * @version 1.0
 */
public enum HotKeyEnum {
    QUESTION("题目", "question"),
    BANK("题库", "bank");
    private final String message;
    private final String key;
    HotKeyEnum(String message, String key) {
        this.message = message;
        this.key = key;
    }
    public String getMessage() {
        return message;
    }
    public String getKey() {
        return key;
    }
}
