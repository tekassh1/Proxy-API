package org.example.vkintership.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfiguration {
    @Value("${proxy.target}") private String baseUrl;

    @Bean
    public WebClient getWebClient() {
        HttpClient client = HttpClient.create().responseTimeout(Duration.ofMillis(2000));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }
}
