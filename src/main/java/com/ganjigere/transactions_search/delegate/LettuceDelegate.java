package com.ganjigere.transactions_search.delegate;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisStringCommands;

public class LettuceDelegate {

    RedisClient client;

    public LettuceDelegate() {
        client = RedisClient.create("redis://localhost");
    }

    public RedisCommands<String, String> getCommands() {
        StatefulRedisConnection<String, String> connection = client.connect();
        return connection.sync();
    }

}
