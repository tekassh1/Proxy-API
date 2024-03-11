package org.example.vkintership.model.caching;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.vkintership.model.common.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public class Cache <T extends Data> {
    private ConcurrentHashMap<Long, T> cache;
    private LocalDateTime lastUpdate;

    public T getById(Long id) {
        return cache.get(id);
    }

    public void setValid() {
        lastUpdate = LocalDateTime.now();
    }

    public boolean isValid() {
        return (lastUpdate != null) && (Duration.between(lastUpdate, LocalDateTime.now()).getSeconds() <= 3);
    }

    public void addValue(Long id, T t) {
        cache.put(id, t);
    }

    public void removeValue(Long id) {
        cache.remove(id);
    }
}