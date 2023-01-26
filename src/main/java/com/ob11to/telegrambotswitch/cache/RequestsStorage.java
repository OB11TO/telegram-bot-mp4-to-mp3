package com.ob11to.telegrambotswitch.cache;

import com.ob11to.telegrambotswitch.dto.Request;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestsStorage {

    private final Map<Long, Request> requests = new ConcurrentHashMap<>();

    public void addRequest(Long chatId, Request request){
        requests.put(chatId,request);
    }

    public void updateRequest(Long chatId, Request request){
        requests.put(chatId,request);
    }

    public void removeRequest(Long chatId){
        requests.remove(chatId);
    }

    public Request getCurrentRequest(Long chatId){
        return requests.get(chatId);
    }

    public boolean isRequestPresent(Long chatId){
        return requests.containsKey(chatId);
    }
}
