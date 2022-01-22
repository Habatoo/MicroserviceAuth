package com.ssport.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class ListApplePublicKey {

    private final List<ApplePublicKey> keys;

    public ListApplePublicKey(@JsonProperty("keys") List<ApplePublicKey> keys) {
        this.keys = keys;
    }

    public List<ApplePublicKey> getKeys() {
        return keys;
    }
}