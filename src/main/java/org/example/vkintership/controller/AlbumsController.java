package org.example.vkintership.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/albums")
public class AlbumsController {

    @Autowired
    WebClient webClient;

    @GetMapping("/{albumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<String> getById(@PathVariable Long albumId) {
        return webClient
                .get()
                .uri(String.join("","/albums/", String.valueOf(albumId)))
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<String> getAll() {
        return webClient
                .get()
                .uri("/albums")
                .retrieve()
                .bodyToMono(String.class);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public String post() {
        return "Post albums!!!";
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public String put() {
        return "Put albums!!!";
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public String delete() {
        return "Delete albums!!!";
    }
}