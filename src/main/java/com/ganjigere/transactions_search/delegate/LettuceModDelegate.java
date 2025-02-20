package com.ganjigere.transactions_search.delegate;


import com.redis.lettucemod.RedisModulesClient;
import com.redis.lettucemod.RedisModulesUtils;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import io.lettuce.core.AbstractRedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class LettuceModDelegate {

    private final AbstractRedisClient client;

    public LettuceModDelegate() {
        this.client = RedisModulesClient.create("redis://localhost:6379");;
    }

    public void afterPropertiesSet() throws Exception {
        log.info("Pinging Redis");
        StatefulRedisModulesConnection<String, String> connection = RedisModulesUtils.connection(client);
        String reply = connection.sync().ping();
        if ("PONG".equalsIgnoreCase(reply)) {
            log.info("Successfully pinged Redis (response: {})", reply);
        } else {
            log.error("Failed to ping Redis (response: {})", reply);
        }

    }

}
