package org.example.vkintership.controller;

import org.example.vkintership.entity.User;
import org.example.vkintership.exceptions.auth.UsernameExistsException;
import org.example.vkintership.exceptions.auth.WrongCredsFormatException;
import org.example.vkintership.model.AuthRequest;
import org.example.vkintership.model.AuthResponse;
import org.example.vkintership.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private UserService userService;

    public AuthenticationController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest authRequest) {
        try {
            User user = new User(authRequest.getUsername(), authRequest.getPassword());
            userService.registerUser(user);

            AuthResponse response = new AuthResponse("Signed up successfully!");
            return ResponseEntity.ok(response);
        }
        catch (UsernameExistsException | WrongCredsFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(e.getMessage()));
        }
    }
}