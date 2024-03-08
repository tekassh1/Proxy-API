package org.example.vkintership.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostsController {

    @GetMapping
    public String get() {
        return "Get posts!!!";
    }

    @PostMapping
    public String post() {
        return "Post posts!!!";
    }

    @PutMapping
    public String put() {
        return "Put posts!!!";
    }

    @DeleteMapping
    public String delete() {
        return "Delete posts!!!";
    }
}