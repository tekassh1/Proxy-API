package org.example.vkintership.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @GetMapping
    public String get() {
        return "Get users!!!";
    }

    @PostMapping
    public String post() {
        return "Post users!!!";
    }

    @PutMapping
    public String put() {
        return "Put users!!!";
    }

    @DeleteMapping
    public String delete() {
        return "Delete users!!!";
    }
}