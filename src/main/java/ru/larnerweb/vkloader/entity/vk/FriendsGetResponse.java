package ru.larnerweb.vkloader.entity.vk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendsGetResponse  implements Serializable {
    FriendsGet response;

    public FriendsGet getResponse() {
        return response;
    }

    public void setResponse(FriendsGet response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "FriendsGetResponse{" +
                "response=" + response +
                '}';
    }
}
