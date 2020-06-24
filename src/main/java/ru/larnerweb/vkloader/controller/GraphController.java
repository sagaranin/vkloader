package ru.larnerweb.vkloader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.larnerweb.vkloader.services.InMemoryGraphService;

import java.util.List;

@RestController("/")
public class GraphController {

    @Autowired
    private InMemoryGraphService graph;

    @GetMapping("/graph/{id}")
    private int[] getFriendsById(@PathVariable Integer id){
        return graph.getFriends(id);
    }

    @GetMapping("/bfs")
    private List<Integer> findPath(@RequestParam int from, @RequestParam int to){
        return graph.bfs(from, to);
    }

}
