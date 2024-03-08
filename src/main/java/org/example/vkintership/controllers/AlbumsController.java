package org.example.vkintership.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/albums")
public class AlbumsController {
    @GetMapping
    public String get() {
        return "Get albums!!!";
    }

    @PostMapping
    public String post() {
        return "Post albums!!!";
    }

    @PutMapping
    public String put() {
        return "Put albums!!!";
    }

    @DeleteMapping
    public String delete() {
        return "Delete albums!!!";
    }
}