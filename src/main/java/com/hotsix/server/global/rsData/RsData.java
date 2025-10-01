package com.hotsix.server.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;


public record RsData<T>(
        @NonNull String resultCode,
        @JsonIgnore int statusCode,
        @NonNull String msg,
        @NonNull T data) {
    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }
    public RsData(String resultCode, String msg, T data) {
        this(resultCode, parseStatusCode(resultCode), msg, data);
    }
    private static int parseStatusCode(String resultCode) {
        try {
            return Integer.parseInt(resultCode.split("-", 2)[0]);
        } catch (NumberFormatException e) {
            return 500; // 기본값
        }
    }
}