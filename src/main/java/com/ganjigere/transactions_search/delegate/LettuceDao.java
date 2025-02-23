package com.ganjigere.transactions_search.delegate;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.json.JsonPath;
import io.lettuce.core.json.JsonValue;

import java.nio.ByteBuffer;

public class LettuceDao implements GenericDao {

    RedisClient client;
    RedisCommands<String, String> redis;

    public LettuceDao() {
        client = RedisClient.create("redis://localhost/");
    }

    public RedisCommands<String, String> getCommands() {
        if(null == redis) {
            StatefulRedisConnection<String, String> connection = client.connect();
            redis = connection.sync();
        }
        return redis;
    }

    @Override
    public void addUpdateRecords(String key, Object value) {
        JsonValue json = getCommands().getJsonParser().createJsonValue((String)value);
        getCommands().jsonSet(key, JsonPath.ROOT_PATH, json);
    }

    @Override
    public Object getRecords(String key, String path) {
        return getCommands().jsonGet(key, JsonPath.of(path));
    }

    @Override
    public void deleteRecords(String key) {

    }

}
