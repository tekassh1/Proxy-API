package org.example.vkintership.model.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Company {
    @NotNull private String name;
    @NotNull private String catchPhrase;
    @NotNull private String bs;
}