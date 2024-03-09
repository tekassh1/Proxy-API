package org.example.vkintership.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/albums")
public class AlbumsController {
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public String get() {
        return "Get albums!!!";
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public String post() {
        return "Post albums!!!";
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public String put() {
        return "Put albums!!!";
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALBUMS', 'ROLE_ADMIN')")
    public String delete() {
        return "Delete albums!!!";
    }
}