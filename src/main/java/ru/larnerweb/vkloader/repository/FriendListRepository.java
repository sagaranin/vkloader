package ru.larnerweb.vkloader.repository;

import io.micrometer.core.annotation.Timed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.larnerweb.vkloader.entity.FriendList;
import ru.larnerweb.vkloader.entity.FriendListBD;

import java.util.List;

public interface FriendListRepository extends PagingAndSortingRepository<FriendList, Integer> {

    @Timed("jpa.find.friends")
    FriendListBD findById(int id);

    @Query(value = "SELECT f.id FROM FriendList f")
    List<Integer> findAllIds();

}
