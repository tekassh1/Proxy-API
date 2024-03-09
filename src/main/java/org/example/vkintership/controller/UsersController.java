package org.example.vkintership.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UsersController {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public String get() {
        return "Get users!!!";
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public String post() {
        return "Post users!!!";
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public String put() {
        return "Put users!!!";
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USERS', 'ROLE_ADMIN')")
    public String delete() {
        return "Delete users!!!";
    }
}