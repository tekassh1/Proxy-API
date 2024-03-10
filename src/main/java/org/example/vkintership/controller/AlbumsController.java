package org.example.vkintership.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vkintership.model.caching.CacheType;
import org.example.vkintership.model.common.AlbumsData;
import org.example.vkintership.service.CachingService;
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

    @Autowired
    CachingService cachingService;

    @GetMapping("/{albumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getById(@PathVariable Long albumId) throws JsonProcessingException {

        if (cachingService.isCacheValid(CacheType.ALBUMS) &&
                cachingService.findEntityById(CacheType.ALBUMS, albumId) != null) {

            ObjectMapper objectMapper = new ObjectMapper();
            return Mono.just(ResponseEntity.ok(
                    objectMapper.writeValueAsString(cachingService.findEntityById(CacheType.ALBUMS, albumId))));
        }
        else if (cachingService.isCacheValid(CacheType.ALBUMS)) {
//            Делаем запрос, добавляем в коллекцию в кэше и возвращаем значение

//            String res = webClient
//                    .get()
//                    .uri(String.join("", "/albums/", String.valueOf(albumId)))
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
        }

        // Обновляем коллекцию полностью и возвращаем значение
        cachingService.updateCache(CacheType.ALBUMS);
        AlbumsData album = (AlbumsData) cachingService.getCache(CacheType.ALBUMS).get(albumId);

        ObjectMapper objectMapper = new ObjectMapper();
        return Mono.just(ResponseEntity.ok().body(
                objectMapper.writeValueAsString(album)
        ));
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