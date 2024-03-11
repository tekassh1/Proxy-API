package org.example.vkintership.model.caching;

import lombok.Getter;
import org.example.vkintership.model.common.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Cache <T extends Data> {
    private ConcurrentHashMap<Long, T> cache;
    private LocalDateTime lastUpdate;
    private Long validityTimeMillis;

    public Cache(ConcurrentHashMap<Long, T> cache, Long validityTime) {
        this.cache = cache;
        this.validityTimeMillis = validityTime;
        this.lastUpdate = null;
    }

    public T getById(Long id) {
        return cache.get(id);
    }

    public void setValid() {
        lastUpdate = LocalDateTime.now();
    }

    public boolean isValid() {
        return (lastUpdate != null) &&
                (Duration.between(lastUpdate, LocalDateTime.now()).toMillis() <= validityTimeMillis);
    }

    public void addValue(Long id, T t) {
        cache.put(id, t);
    }

    public void removeValue(Long id) {
        cache.remove(id);
    }
}