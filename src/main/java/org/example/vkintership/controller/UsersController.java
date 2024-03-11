package org.example.vkintership.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vkintership.model.caching.Cache;
import org.example.vkintership.model.caching.CacheType;
import org.example.vkintership.model.common.UsersData;
import org.example.vkintership.service.CachingService;
import org.example.vkintership.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
public class UsersController {

    @Autowired
    WebClient webClient;
    @Autowired
    WebService webService;

    @Autowired
    CachingService cachingService;

    @GetMapping("api/users/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getUserById(@PathVariable Long userId)
            throws JsonProcessingException, HttpClientErrorException {

        Cache cache = cachingService.getCache(CacheType.USERS);
        ObjectMapper objectMapper = new ObjectMapper();

        if (cache.isValid() && cache.getById(userId) != null) {
            return Mono.just(ResponseEntity.ok(
                    objectMapper.writeValueAsString(cache.getById(userId))));
        } else if (cache.isValid()) {
            String res = webClient
                    .get()
                    .uri(String.join("", "/users/", String.valueOf(userId)))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                    )
                    .bodyToMono(String.class)
                    .block();

            UsersData data = objectMapper.readValue(res, UsersData.class);
            cache.addValue(data.getId(), data);
            return Mono.just(ResponseEntity.ok().body(res));
        }

        cachingService.updateCache(CacheType.USERS);

        UsersData user = (UsersData) cache.getById(userId);
        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(user)));
    }

    @GetMapping(value = {"api/users", "api/users/"})
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getAllUsers() throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.USERS);
        ObjectMapper objectMapper = new ObjectMapper();

        if (!cache.isValid())
            cachingService.updateCache(CacheType.USERS);

        Collection<UsersData> tmp = cache.getCache().values();
        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(tmp)));
    }

    @PostMapping(value = {"api/users", "api/users/"})
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> createUser(@RequestBody UsersData usersData) throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.USERS);

        String res = webClient
                .post()
                .uri("/users")
                .body(Mono.just(usersData), UsersData.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                )
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        UsersData user = objectMapper.readValue(res, UsersData.class);
        cache.removeValue(user.getId());
        cache.addValue(user.getId(), user);

        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(user)));
    }

    @PutMapping("api/users/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> updateUser(@PathVariable Long userId,
                                                   @RequestBody UsersData usersData) throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.USERS);

        if (cache.isValid() && cache.getById(userId) == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User for updating not found!"));
        }

        String res = webClient
                .put()
                .uri(String.join("", "/users/", String.valueOf(userId)))
                .body(Mono.just(usersData), UsersData.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                )
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        UsersData user = objectMapper.readValue(res, UsersData.class);
        cache.removeValue(userId);
        cache.addValue(userId, user);

        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(user)));
    }

    @DeleteMapping("api/users/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable Long userId) {

        Cache cache = cachingService.getCache(CacheType.USERS);
        if (cache.getById(userId) == null)
            Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User for deleting not found!"));

        cache.removeValue(userId);

        return webClient
                .delete()
                .uri(String.join("", "/users/", String.valueOf(userId)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                )
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }
}