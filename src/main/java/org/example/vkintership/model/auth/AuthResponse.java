package org.example.vkintership.model.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private String message;

    public AuthResponse(String message) {
        this.message = message;
    }

    public AuthResponse(){};
}