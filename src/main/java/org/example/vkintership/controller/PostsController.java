package org.example.vkintership.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/posts")
public class PostsController {

    @Autowired
    WebClient webClient;

    @GetMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public Mono<String> getById(@PathVariable Long postId) {
        return webClient
                .get()
                .uri(String.join("","/posts/", String.valueOf(postId)))
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public Mono<String> getAll() {
        return webClient
                .get()
                .uri("/posts")
                .retrieve()
                .bodyToMono(String.class);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public String post() {
        return "Post posts!!!";
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public String put() {
        return "Put posts!!!";
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public String delete() {
        return "Delete posts!!!";
    }
}