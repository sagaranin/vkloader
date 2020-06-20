package ru.larnerweb.vkloader.entity.vk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public class Response implements Serializable {
    Integer count;
    List<Integer>  items;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Integer>  getItems() {
        return items;
    }

    public void setItems(List<Integer>  items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "count=" + count +
                '}';
    }
}
