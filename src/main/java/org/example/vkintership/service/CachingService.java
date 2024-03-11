package org.example.vkintership.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vkintership.model.caching.Cache;
import org.example.vkintership.model.caching.CacheType;
import org.example.vkintership.model.common.AlbumsData;
import org.example.vkintership.model.common.Data;
import org.example.vkintership.model.common.PostsData;
import org.example.vkintership.model.common.UsersData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CachingService <T extends Data> {

    @Autowired
    WebClient webClient;
    @Autowired
    WebService webService;

    private ConcurrentHashMap<CacheType, Cache> cachesList = new ConcurrentHashMap<>();

    public CachingService() {
        cachesList.put(CacheType.POSTS, new Cache(new ConcurrentHashMap<Long, PostsData>(), null));
        cachesList.put(CacheType.ALBUMS, new Cache(new ConcurrentHashMap<Long, AlbumsData>(), null));
        cachesList.put(CacheType.USERS, new Cache(new ConcurrentHashMap<Long, UsersData>(), null));
    }

    public Cache getCache(CacheType type) {
        return cachesList.get(type);
    }

    public boolean isCacheValid(CacheType type) {
        return cachesList.get(type).isValid();
    }

    public void invalidateCache(CacheType type) {
        cachesList.get(type).setValid();
    }

    public void updateCache(CacheType cacheType) throws JsonProcessingException {
        switch (cacheType){

            case ALBUMS -> {
                cachesList.get(CacheType.ALBUMS).getCache().clear();

                String res = webClient
                        .get()
                        .uri("/albums")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                ObjectMapper objectMapper = new ObjectMapper();
                List<AlbumsData> tmp = objectMapper.readValue(res, new TypeReference<>() {});
                tmp.forEach(value -> cachesList.get(CacheType.ALBUMS).addValue(value.getId(), value));
                cachesList.get(CacheType.ALBUMS).setValid();
            }
            case POSTS -> {
                cachesList.get(CacheType.POSTS).getCache().clear();

                String res = webClient
                        .get()
                        .uri("/posts")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                ObjectMapper objectMapper = new ObjectMapper();
                List<PostsData> tmp = objectMapper.readValue(res, new TypeReference<>() {});
                tmp.forEach(value -> cachesList.get(CacheType.POSTS).addValue(value.getId(), value));
                cachesList.get(CacheType.POSTS).setValid();
            }
            case USERS -> {
                cachesList.get(CacheType.USERS).getCache().clear();

                String res = webClient
                        .get()
                        .uri("/users")
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorReturn("false") // В случае ошибки возвращаем "false"
                        .block();

                ObjectMapper objectMapper = new ObjectMapper();
                List<UsersData> tmp = objectMapper.readValue(res, new TypeReference<>() {});
                tmp.forEach(value -> cachesList.get(CacheType.USERS).addValue(value.getId(), value));
                cachesList.get(CacheType.USERS).setValid();
            }
        }
    }

    @Scheduled(fixedRate = 3000)
    public void checkCache()  throws JsonProcessingException {
        for (var entry : cachesList.entrySet()) {
            System.out.println("Cache " + entry.getKey().toString() + " was updated!");
            if (!entry.getValue().isValid()) updateCache(entry.getKey());
        }
        System.out.println("\n");
    }
}