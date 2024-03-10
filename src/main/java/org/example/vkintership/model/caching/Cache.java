package org.example.vkintership.model.caching;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.vkintership.model.common.Data;

import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public class Cache <T extends Data> {
    private ConcurrentHashMap<Long, T> cache;
    private volatile boolean isValid;

    public T getById(Long id) {
        return cache.get(id);
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public void addValue(Long id, T t) {
        cache.put(id, t);
    }
}