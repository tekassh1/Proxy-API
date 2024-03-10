package org.example.vkintership.model.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UsersData {
    private Long id;
    @NotNull private String name;
    @NotNull private String username;
    @NotNull private String email;
    @NotNull private Address address;
    @NotNull private String phone;
    @NotNull private String website;
    @NotNull private Company company;
}