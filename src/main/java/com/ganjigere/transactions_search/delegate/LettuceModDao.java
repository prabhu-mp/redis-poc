package com.ganjigere.transactions_search.delegate;


import com.redis.lettucemod.RedisModulesClient;
import com.redis.lettucemod.RedisModulesUtils;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisModulesCommands;
import com.redis.lettucemod.search.CreateOptions;
import com.redis.lettucemod.search.Field;
import com.redis.lettucemod.search.TextField;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.json.JsonPath;
import io.lettuce.core.json.JsonValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class LettuceModDao implements GenericDao {

    private final AbstractRedisClient client;
    RedisModulesCommands<String, String> redis;

    //public static Schema BICYCLE_SCHEMA = Schema.builder()

    public LettuceModDao() {
        this.client = RedisModulesClient.create("redis://localhost:6379");
        log.info("Pinging Redis");
        StatefulRedisModulesConnection<String, String> connection = RedisModulesUtils.connection(client);
        String reply = connection.sync().ping();
        RedisModulesCommands<String, String> redis = connection.sync();
        if ("PONG".equalsIgnoreCase(reply)) {
            log.info("Successfully pinged Redis (response: {})", reply);
        } else {
            log.error("Failed to ping Redis (response: {})", reply);
        }
    }

    public RedisModulesCommands<String, String> getCommands() {
        if (null == redis) {
            StatefulRedisModulesConnection<String, String> connection = RedisModulesUtils.connection(client);
            redis = connection.sync();
        }
        return redis;
    }

    @Override
    public void addUpdateRecords(String key, Object value) {
        JsonValue json = getCommands().getJsonParser().createJsonValue((String) value);
        getCommands().jsonSet(key, JsonPath.ROOT_PATH, json);
    }

    @Override
    public Object getRecords(String key, String path) {
        return getCommands().jsonGet(key, JsonPath.of(path));
    }

    @Override
    public void deleteRecords(String key) {

    }

    public void createIndex() {
        TextField<String> tagField = Field.text("$.description").as("description").build();
        Field<String>[] fields = new Field[]{tagField};
        CreateOptions<String, String> idxOptions = CreateOptions.<String, String>builder()
                        .prefix("sample_bicycle")
                .on(CreateOptions.DataType.JSON)
                .build();
        getCommands().ftCreate("bicycleIdx", idxOptions, fields);
    }

    public Object search(String idx, String path) {
        return getCommands().ftSearch(idx, path);
    }

}
