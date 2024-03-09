package org.example.vkintership.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
public class PostsController {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public String get() {
        return "Get posts!!!";
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public String post() {
        return "Post posts!!!";
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public String put() {
        return "Put posts!!!";
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_POSTS', 'ROLE_ADMIN')")
    public String delete() {
        return "Delete posts!!!";
    }
}