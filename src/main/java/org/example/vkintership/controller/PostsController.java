package org.example.vkintership.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vkintership.model.caching.Cache;
import org.example.vkintership.model.caching.CacheType;
import org.example.vkintership.model.common.PostsData;
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
public class PostsController {

    @Autowired
    WebClient webClient;
    @Autowired
    WebService webService;

    @Autowired
    CachingService cachingService;

    @GetMapping("api/posts/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_POSTS', 'ROLE_POSTS_VIEWER')")
    public Mono<ResponseEntity<String>> getPostById(@PathVariable Long postId)
            throws JsonProcessingException, HttpClientErrorException {

        Cache cache = cachingService.getCache(CacheType.POSTS);
        ObjectMapper objectMapper = new ObjectMapper();

        if (cache.isValid() && cache.getById(postId) != null) {
            return Mono.just(ResponseEntity.ok(
                    objectMapper.writeValueAsString(cache.getById(postId))));
        } else if (cache.isValid()) {
            String res = webClient
                    .get()
                    .uri(String.join("", "/posts/", String.valueOf(postId)))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                    )
                    .bodyToMono(String.class)
                    .block();

            if (res == null)
                return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested id was not found!"));

            PostsData data = objectMapper.readValue(res, PostsData.class);
            cache.addValue(data.getId(), data);
            return Mono.just(ResponseEntity.ok().body(res));
        }

        cachingService.updateCache(CacheType.POSTS);

        PostsData post = (PostsData) cache.getById(postId);
        if (post == null)
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested id was not found!"));
        else
            return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(post)));
    }

    @GetMapping(value = {"api/posts", "api/posts/"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_POSTS', 'ROLE_POSTS_VIEWER')")
    public Mono<ResponseEntity<String>> getAllPosts() throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.POSTS);
        ObjectMapper objectMapper = new ObjectMapper();

        if (!cache.isValid())
            cachingService.updateCache(CacheType.POSTS);

        Collection<PostsData> tmp = cache.getCache().values();
        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(tmp)));
    }

    @PostMapping(value = {"api/posts", "api/posts/"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_POSTS', 'ROLE_POSTS_EDITOR')")
    public Mono<ResponseEntity<String>> createPost(@RequestBody PostsData postsData) throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.POSTS);

        String res = webClient
                .post()
                .uri("/posts")
                .body(Mono.just(postsData), PostsData.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                )
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        PostsData post = objectMapper.readValue(res, PostsData.class);
        cache.removeValue(post.getId());
        cache.addValue(post.getId(), post);

        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(post)));
    }

    @PutMapping("api/posts/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_POSTS', 'ROLE_POSTS_EDITOR')")
    public Mono<ResponseEntity<String>> updatePost(@PathVariable Long postId,
                                                   @RequestBody PostsData postsData) throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.POSTS);

        if (cache.isValid() && cache.getById(postId) == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post for updating not found!"));
        }

        String res = webClient
                .put()
                .uri(String.join("", "/posts/", String.valueOf(postId)))
                .body(Mono.just(postsData), PostsData.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                )
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        PostsData post = objectMapper.readValue(res, PostsData.class);
        cache.removeValue(postId);
        cache.addValue(postId, post);

        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(post)));
    }

    @DeleteMapping("api/posts/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_POSTS', 'ROLE_POSTS_EDITOR')")
    public Mono<ResponseEntity<String>> deletePost(@PathVariable Long postId) throws JsonProcessingException {

        Cache cache = cachingService.getCache(CacheType.POSTS);
        if (cache.getById(postId) == null)
            Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post for deleting not found!"));

        cache.removeValue(postId);

        String res = webClient
                .delete()
                .uri(String.join("", "/posts/", String.valueOf(postId)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new HttpClientErrorException(clientResponse.statusCode()))
                )
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        return Mono.just(ResponseEntity.ok().body(objectMapper.writeValueAsString(res)));
    }
}