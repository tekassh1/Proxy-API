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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        cachesList.put(CacheType.POSTS, new Cache(new ConcurrentHashMap<Long, PostsData>(), 5000L));
        cachesList.put(CacheType.ALBUMS, new Cache(new ConcurrentHashMap<Long, AlbumsData>(), 5000L));
        cachesList.put(CacheType.USERS, new Cache(new ConcurrentHashMap<Long, UsersData>(), 5000L));
    }

    public Cache getCache(CacheType type) {
        return cachesList.get(type);
    }

    public void updateCache(CacheType cacheType) throws JsonProcessingException {
        switch (cacheType){
            case ALBUMS -> {
                cachesList.get(CacheType.ALBUMS).getCache().clear();

                String res = webClient
                        .get()
                        .uri("/albums")
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                        )
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
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                        )
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
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                        )
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

    @Scheduled(fixedRate = 5000)
    public void checkCache()  throws JsonProcessingException {
        for (var entry : cachesList.entrySet()) {
            if (!entry.getValue().isValid()) updateCache(entry.getKey());
        }
    }
}