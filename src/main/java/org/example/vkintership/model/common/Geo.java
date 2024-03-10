package org.example.vkintership.model.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Geo {
    @NotNull private Double lat;
    @NotNull private Double lng;
}