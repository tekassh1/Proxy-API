package org.example.vkintership.controller;

import org.example.vkintership.model.common.PostsData;
import org.example.vkintership.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/posts")
public class PostsController {

    @Autowired
    WebClient webClient;
    @Autowired
    WebService webService;

    @GetMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getById(@PathVariable Long postId) {
        return webClient
                .get()
                .uri(String.join("", "/posts/", String.valueOf(postId)))
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getAll() {
        return webClient
                .get()
                .uri("/posts")
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> post(@RequestBody PostsData postsData) {
        return webClient
                .post()
                .uri("/posts")
                .body(Mono.just(postsData), PostsData.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> put(@PathVariable Long postId,
                                            @RequestBody PostsData postsData) {
        // Изменяем в кэше, если инвалид => отклоняем, а уже потом запрос
        return webClient
                .put()
                .uri(String.join("", "/posts/", String.valueOf(postId)))
                .body(Mono.just(postsData), PostsData.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> delete(@PathVariable Long postId) {
        return webClient
                .delete()
                .uri(String.join("", "/posts/", String.valueOf(postId)))
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }
}