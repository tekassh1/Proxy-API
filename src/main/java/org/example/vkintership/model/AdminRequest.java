package org.example.vkintership.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminRequest {
    private String username;
    private String password;
    private String role;
}
