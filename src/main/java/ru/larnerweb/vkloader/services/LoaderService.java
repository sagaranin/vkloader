package ru.larnerweb.vkloader.services;

import org.hibernate.PropertyAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.larnerweb.vkloader.entity.FriendList;
import ru.larnerweb.vkloader.repository.FriendListRepository;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Service
public class LoaderService {

    private static final Logger log = LoggerFactory.getLogger(LoaderService.class);

    @Value("${app.redis.update_cache}")
    private String init;

    @Autowired
    RedisService redisService;

    @Autowired
    VKClientService vkClientService;

    @Autowired
    FriendListRepository friendListRepository;


    @Scheduled(fixedDelay = 200L)
    private void load(){
        String nextId = redisService.pop();
        log.info("Fetching {} from VK", nextId);
        List<Integer> friends = vkClientService.getFriends(Integer.parseInt(nextId));

        try {
            FriendList fl = new FriendList(Integer.parseInt(nextId), friends);
            log.info(fl.toString());
            friendListRepository.save(fl);
            redisService.addToScanned(nextId);

        } catch (Exception e){
            log.error(e.getMessage());
            System.exit(1);
        }

        for (Integer i : friends){
            if (!redisService.isScanned(String.valueOf(i))) redisService.addToQueue(String.valueOf(i));
        }

    }


    @PostConstruct
    private void init() {
        log.info("Init method started...");

        if (init.toLowerCase().equals("true")) {
            for (Integer id : friendListRepository.findAllIds()) {
                redisService.addToScanned(String.valueOf(id));
            }
        }

        log.info("Init method finished...");
    }
}
