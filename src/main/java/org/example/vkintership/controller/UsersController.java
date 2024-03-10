package org.example.vkintership.controller;

import org.example.vkintership.model.common.UsersData;
import org.example.vkintership.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/users")
public class UsersController {

    @Autowired
    WebClient webClient;
    @Autowired
    WebService webService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getById(@PathVariable Long userId) {
        return webClient
                .get()
                .uri(String.join("", "/users/", String.valueOf(userId)))
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getAll() {
        return webClient
                .get()
                .uri("/users")
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> post(@RequestBody UsersData usersData) {
        return webClient
                .post()
                .uri("/users")
                .body(Mono.just(usersData), UsersData.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> put(@PathVariable Long userId,
                                            @RequestBody UsersData usersData) {

        return webClient
                .put()
                .uri(String.join("", "/users/", String.valueOf(userId)))
                .body(Mono.just(usersData), UsersData.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> delete(@PathVariable Long userId) {
        return webClient
                .delete()
                .uri(String.join("", "/users/", String.valueOf(userId)))
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }
}