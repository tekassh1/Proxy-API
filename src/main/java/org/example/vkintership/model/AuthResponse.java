package org.example.vkintership.model;

public class AuthResponse {
    private String message;

//    private String accessToken;

    public AuthResponse(String message) {
        this.message = message;
    }

    public AuthResponse(){};

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}