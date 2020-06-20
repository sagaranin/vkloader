package ru.larnerweb.vkloader.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.larnerweb.vkloader.entity.vk.FriendsGetResponse;

import java.util.List;
import java.util.Random;

/**
 * vk api client
 */
@Service
public class VKClientService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${app.vcs.keys}")
    private String keys;


    private List<Integer> getFriends(int id){
        String key = keys.split(",")[getRandomNumberInRange(0, keys.split(",").length)];

        FriendsGetResponse fgr = restTemplate.getForObject(
                String.format("https://api.vk.com/method/friends.get?id=%s&count=10000&access_token=%s&v=5.8", id, key),
                FriendsGetResponse.class
        );

        if (fgr != null){
            return fgr.getFriends().getItems();
        } else {
            return null;
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
