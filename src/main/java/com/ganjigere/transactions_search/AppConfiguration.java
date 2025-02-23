package com.ganjigere.transactions_search;

import com.ganjigere.transactions_search.business.SearchService;
import com.ganjigere.transactions_search.delegate.JedisDao;
import com.ganjigere.transactions_search.delegate.LettuceDao;
import com.ganjigere.transactions_search.delegate.LettuceModDao;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    @ConditionalOnProperty(name = "app.redis.client", havingValue = "lettuce", matchIfMissing = true)
    public SearchService createLettuceSearch() {
        return new SearchService(new LettuceDao());
    }

    @Bean
    @ConditionalOnProperty(name = "app.redis.client", havingValue = "jedis", matchIfMissing = false)
    public SearchService createJedisSearch() {
        return new SearchService(new JedisDao());
    }

    @Bean
    @ConditionalOnProperty(name = "app.redis.client", havingValue = "lettucemod", matchIfMissing = false)
    public SearchService createLettuceModSearch() {
        return new SearchService(new LettuceModDao());
    }
}
