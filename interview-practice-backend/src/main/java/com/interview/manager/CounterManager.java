package com.interview.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author hjc
 * @version 1.0
 */
@Slf4j
@Service
public class CounterManager {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 默认统计一分钟内的计数
     * @param key
     * @return
     */
    public long incrAndGetCounter(String key) {
        return incrAndGetCounter(key, 1, TimeUnit.SECONDS);
    }

    /**
     * 增加并返回计数
     * @param key
     * @param timeInterval
     * @param timeUnit
     * @return
     */
    public long incrAndGetCounter(String key, int timeInterval, TimeUnit timeUnit) {
        long expirationTimeInSeconds;
        switch(timeUnit) {
            case SECONDS:
                expirationTimeInSeconds = timeInterval;
                break;
            case MINUTES:
                expirationTimeInSeconds = timeInterval * 60;
                break;
            case HOURS:
                expirationTimeInSeconds = timeInterval * 60 * 60;
                break;
            default:
                throw new IllegalArgumentException("不支持的时间单位");
        }
        return incrAndGetCounter(key, timeInterval, timeUnit, expirationTimeInSeconds);
    }

    /**
     * 增加并返回计数
     * @param key
     * @param timeInterval
     * @param timeUnit
     * @param expirationTimeInSeconds
     * @return
     */
    public long incrAndGetCounter(String key, int timeInterval, TimeUnit timeUnit, long expirationTimeInSeconds) {
        if (StringUtils.isBlank(key)) {
            return 0;
        }
        long timeFactor;
        switch(timeUnit) {
            case SECONDS:
                timeFactor = Instant.now().getEpochSecond() / timeInterval;
                break;
            case MINUTES:
                timeFactor = Instant.now().getEpochSecond() / timeInterval / 60;
                break;
            case HOURS:
                timeFactor = Instant.now().getEpochSecond() / timeInterval / 3600;
                break;
            default:
                throw new IllegalArgumentException("不支持的时间单位");
        }
        String redisKey = key + ":" + timeFactor;

        String luaScript = "if redis.call('exists', KEYS[1]) == 1 then" +
                "    return redis.call('incr', KEYS[1]); " +
                "else" +
                "    redis.call('set', KEYS[1], 1)" +
                "    redis.call('expire', KEYS[1], ARGV[1])" +
                "    return 1; " +
                "end";
        RScript script = redissonClient.getScript(IntegerCodec.INSTANCE);
        Object countObj = script.eval(RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(redisKey),
                expirationTimeInSeconds);
        return (long) countObj;
    }
}
