package org.example.vkintership.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vkintership.model.caching.Cache;
import org.example.vkintership.model.caching.CacheType;
import org.example.vkintership.model.common.AlbumsData;
import org.example.vkintership.service.CachingService;
import org.example.vkintership.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;

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

        Cache cache = cachingService.getCache(CacheType.ALBUMS);
        ObjectMapper objectMapper = new ObjectMapper();

        if (cache.isValid() &&
                cache.getById(albumId) != null) {

            return Mono.just(ResponseEntity.ok(
                    objectMapper.writeValueAsString(cache.getById(albumId))));
        }
        else if (cache.isValid()) {
            String res = webClient
                    .get()
                    .uri(String.join("", "/albums/", String.valueOf(albumId)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            AlbumsData data = objectMapper.readValue(res, AlbumsData.class);
            cache.addValue(data.getId(), data);
            Mono.just(ResponseEntity.ok().body(res));
        }

        cachingService.updateCache(CacheType.ALBUMS);

        AlbumsData album = (AlbumsData) cache.getById(albumId);
        return Mono.just(ResponseEntity.ok().body(
                objectMapper.writeValueAsString(album)
        ));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> getAll() throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.ALBUMS);
        ObjectMapper objectMapper = new ObjectMapper();

        if (!cache.isValid())
            cachingService.updateCache(CacheType.ALBUMS);

        Collection<AlbumsData> tmp = cache.getCache().values();
        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(tmp)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> post(@RequestBody AlbumsData albumsData) throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.ALBUMS);

        String res = webClient
                .post()
                .uri("/albums")
                .body(Mono.just(albumsData), AlbumsData.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        AlbumsData album = objectMapper.readValue(res, AlbumsData.class);
        cache.removeValue(album.getId());
        cache.addValue(album.getId(), album);

        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(album)));
    }

    @PutMapping("/{albumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> put(@PathVariable Long albumId,
                            @RequestBody AlbumsData albumsData) throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.ALBUMS);

        if (cache.isValid() && cache.getById(albumId) == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User for updating not found!"));
        }

        String res = webClient
                .put()
                .uri(String.join("", "/albums/", String.valueOf(albumId)))
                .body(Mono.just(albumsData), AlbumsData.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        AlbumsData album = objectMapper.readValue(res, AlbumsData.class);
        cache.removeValue(albumId);
        cache.addValue(albumId, album);

        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(album)));
    }

    @DeleteMapping("/{albumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> delete(@PathVariable Long albumId) {

        Cache cache = cachingService.getCache(CacheType.ALBUMS);
        if (cache.getById(albumId) == null)
            Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User for deleting not found!"));

        cache.removeValue(albumId);

        return webClient
                .delete()
                .uri(String.join("", "/albums/", String.valueOf(albumId)))
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> webService.handleErrorResponse(throwable));
    }
}