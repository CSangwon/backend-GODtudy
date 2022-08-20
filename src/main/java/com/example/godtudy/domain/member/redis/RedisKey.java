package com.example.godtudy.domain.member.redis;

import lombok.Getter;

@Getter
public enum RedisKey {
    REFRESH("REFRESH_"), EAUTH("EAUTH_"), ACCESS("Bearer_");

    private String key;

    RedisKey(String key) {
        this.key = key;
    }

}
