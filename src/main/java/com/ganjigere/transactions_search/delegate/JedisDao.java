package com.ganjigere.transactions_search.delegate;

import redis.clients.jedis.UnifiedJedis;

public class JedisDao implements GenericDao {

    private UnifiedJedis getJedis() {
        return new UnifiedJedis("redis://localhost:6379");
    }

    private void close(UnifiedJedis jedis) {
        jedis.close();
    }


    @Override
    public void addUpdateRecords(String key, Object value) {

    }

    @Override
    public Object getRecords(String key, String path) {

        return null;
    }

    @Override
    public void deleteRecords(String key) {

    }
}
