package ru.larnerweb.vkloader.repository;

import io.micrometer.core.annotation.Timed;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.larnerweb.vkloader.entity.FriendListBD;

public interface FriendListBDRepository extends PagingAndSortingRepository<FriendListBD, Integer> {

    @Timed("jpa.find.friends")
    FriendListBD findById(int id);

}
