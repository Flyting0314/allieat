package com.entity;

import com.fasterxml.jackson.annotation.JsonValue;


//=========點數發放狀態 enum ==========
public enum Status {
    PENDING(0),     // 未發放
    COMPLETED(1),   // 已發放
    STOPPED(2);     // 停止發放
   

    private final int value;

    Status(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static Status fromValue(int value) {
        for (Status status : Status.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的狀態值: " + value);
    }

}
