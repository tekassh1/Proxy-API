package org.example.vkintership.repository;

import org.example.vkintership.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepository extends JpaRepository<LogEntity, Long> {
    public LogEntity findByUserId(Long userId);
}