package com.interview.constant;

/**
 * @author hjc
 * @version 1.0
 */
public interface RedisConstant {
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "interview:user:signins";

    /**
     * 获取用户签到 Redis key
     * @param year
     * @param userId
     * @return
     */
    static String getUserSignInRedisKeyPrefix(int year, long userId) {
        return String.format("%s:%s:%s", USER_SIGN_IN_REDIS_KEY_PREFIX, year, userId);
    }
}
