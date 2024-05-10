package org.book.commerce.common.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;

@Configuration
@EnableCaching
@EnableRedisRepositories
public class RedisConfig {


    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    public RedisConnectionFactory redisConnectionFactory(){
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost,redisPort);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }


    @Bean
    public RedisTemplate<String,Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        // RedisTemplate에 설정
        redisTemplate.setDefaultSerializer(serializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * write-back 전략을 사용하는 경우, db에 업데이트한 후에 캐시가 삭제되어야하므로, 스케줄러로 삭제하되, 오류날경우를 대비해 업데이트 시간보다 긴 시간으로 유효시간 잡기
     */
    @Bean
    public RedisCacheManager redisCacheManager(){
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // 키는 보통 문자열로 지정하니까 StringRedisSerializer 사용
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) // 값의 경우 dto 객체를 저장할 수도 있으므로 JSON문자열로 직렬화하고 역직렬화할 수 있는 Serializer사용
                .entryTtl(Duration.ofMinutes(30L)); // 캐시의 유효시간을 30분으로 설정

        HashMap<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("productStockCache",RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30L)));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(redisCacheConfiguration).build();
    }
}
