package org.example.vkintership.controller;

import org.example.vkintership.model.common.AlbumsData;
import org.example.vkintership.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/albums")
public class AlbumsController {

    @Autowired
    WebClient webClient;
    @Autowired
    WebService webService;

    @GetMapping("/{albumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getById(@PathVariable Long albumId) {
        return webClient
                .get()
                .uri(String.join("", "/albums/", String.valueOf(albumId)))
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getAll() {
        return webClient
                .get()
                .uri("/albums")
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> post(@RequestBody AlbumsData albumsData) {
        return webClient
                .post()
                .uri("/albums")
                .body(Mono.just(albumsData), AlbumsData.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @PutMapping("/{albumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> put(@PathVariable Long albumId,
                            @RequestBody AlbumsData albumsData) {

        return webClient
                .put()
                .uri(String.join("", "/albums/", String.valueOf(albumId)))
                .body(Mono.just(albumsData), AlbumsData.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }

    @DeleteMapping("/{albumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> delete(@PathVariable Long albumId) {
        return webClient
                .delete()
                .uri(String.join("", "/albums/", String.valueOf(albumId)))
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }
}