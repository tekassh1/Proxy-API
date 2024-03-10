package org.example.vkintership.model.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AlbumsData {
    @NotNull private Long userId;
    private Long id;
    @NotNull private String title;
}