package ru.larnerweb.vkloader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    private final String SET_NAME = "queue";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public String pop(){
        return redisTemplate.opsForSet().pop(SET_NAME);
    }

    public void add(String id){
        redisTemplate.opsForSet().add(SET_NAME, id);
    }


}
