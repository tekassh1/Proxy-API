package org.example.vkintership.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    private Long userId;
    private String endpoint;
    private String requestType;
    private Timestamp requestTime;
    private int httpStatus;
    private Long param;
}