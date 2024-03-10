package org.example.vkintership.model.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Address {
    @NotNull private String street;
    @NotNull private String suite;
    @NotNull private String city;
    @NotNull private String zipcode;
    @NotNull private Geo geo;
}