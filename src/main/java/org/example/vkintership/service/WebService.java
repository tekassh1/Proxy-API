package org.example.vkintership.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class WebService {
    public Mono<ResponseEntity<String>> handleErrorResponse(Throwable throwable) {
        WebClientResponseException responseException = (WebClientResponseException) throwable;
        HttpStatusCode statusCode = responseException.getStatusCode();
        return Mono.just(ResponseEntity.status(statusCode.value()).body(throwable.getMessage()));
    }
}
