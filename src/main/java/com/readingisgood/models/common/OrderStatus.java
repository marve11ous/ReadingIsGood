package com.readingisgood.models.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    OK((short) 0), FAILED((short) 1);

    private final short code;

    public static OrderStatus fromCode(Short code) {
        return switch (code) {
            case 0 -> OK;
            case 1 -> FAILED;
            default -> null;
        };
    }
}