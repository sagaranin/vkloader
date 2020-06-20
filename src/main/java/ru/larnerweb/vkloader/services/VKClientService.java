package ru.larnerweb.vkloader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.larnerweb.vkloader.entity.vk.FriendsGetResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * vk api client
 */
@Service
public class VKClientService {

    private static final Logger log = LoggerFactory.getLogger(VKClientService.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${app.vcs.keys}")
    private String keys;


    public List<Integer> getFriends(int id){
        String key = keys.split(",")[getRandomNumberInRange(0, keys.split(",").length-1)];
        log.info("Key: {}",key);
        String url = String.format("https://api.vk.com/method/friends.get?user_id=%s&count=10000&access_token=%s&v=5.110", id, key);

        log.info("Using url: {}", url);
        FriendsGetResponse fgr = restTemplate.getForObject(url, FriendsGetResponse.class);

        log.info("Result: {}", fgr);
        if (fgr != null && fgr.getResponse() != null){
            return fgr.getResponse().getItems();
        } else {
            return new ArrayList<>();
        }

    }


    private int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
