package ru.larnerweb.vkloader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;




@Service
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    private final String QUEUE_NAME = "queue";
    private final String CACHE_NAME = "cache";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public String pop(){
        return redisTemplate.opsForSet().pop(QUEUE_NAME);
    }

    public void addToQueue(String id){
        redisTemplate.opsForSet().add(QUEUE_NAME, id);
    }

    public boolean isScanned(String id){
        return redisTemplate.opsForSet().isMember(CACHE_NAME, id);
    }

    public void addToScanned(String id){
        redisTemplate.opsForSet().add(CACHE_NAME, id);
    }

}
