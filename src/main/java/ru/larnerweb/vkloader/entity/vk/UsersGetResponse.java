package ru.larnerweb.vkloader.entity.vk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersGetResponse {

    List<UsersGet> response;

    public List<UsersGet> getResponse() {
        return response;
    }

    public void setResponse(List<UsersGet> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "UsersGetResponse{" +
                "response=" + response +
                '}';
    }
}
