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
        cachesList.put(CacheType.POSTS, new Cache(new ConcurrentHashMap<Long, PostsData>(), false));
        cachesList.put(CacheType.ALBUMS, new Cache(new ConcurrentHashMap<Long, AlbumsData>(), false));
        cachesList.put(CacheType.USERS, new Cache(new ConcurrentHashMap<Long, UsersData>(), false));
    }

    public T findEntityById(CacheType type, Long key) {
        return (T) cachesList.get(type).getById(key);
    }

    public ConcurrentHashMap<Long, T> getCache(CacheType type) {
        return cachesList.get(type).getCache();
    }

    public boolean isCacheValid(CacheType type) {
        return cachesList.get(type).isValid();
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
                ConcurrentHashMap<Long, AlbumsData> tmp = objectMapper.readValue(res, new TypeReference<>() {});
                tmp.forEach((key, value) -> cachesList.get(CacheType.ALBUMS).addValue(key, value));
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
                ConcurrentHashMap<Long, PostsData> tmp = objectMapper.readValue(res, new TypeReference<>() {});
                tmp.forEach((key, value) -> cachesList.get(CacheType.POSTS).addValue(key, value));
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
                ConcurrentHashMap<Long, UsersData> tmp = objectMapper.readValue(res, new TypeReference<>() {});
                tmp.forEach((key, value) -> cachesList.get(CacheType.USERS).addValue(key, value));
            }
        }
    }
}