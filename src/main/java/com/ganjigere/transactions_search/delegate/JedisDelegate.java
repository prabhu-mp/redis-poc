package com.ganjigere.transactions_search.delegate;

import redis.clients.jedis.UnifiedJedis;

public class JedisDelegate {

    private UnifiedJedis getJedis() {
        return new UnifiedJedis("redis://localhost:6379");
    }

    private void close(UnifiedJedis jedis) {
        jedis.close();
    }
}
