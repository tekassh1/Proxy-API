package org.example.vkintership.controller;

import org.example.vkintership.entity.Role;
import org.example.vkintership.entity.User;
import org.example.vkintership.model.admin.AdminRequest;
import org.example.vkintership.repository.RoleRepository;
import org.example.vkintership.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/setRole")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> setRole(@RequestBody AdminRequest adminRequest) {
        Optional<User> optUser = userRepository.findByUsername(adminRequest.getUsername());

        if (optUser.isEmpty())
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found!"));

        Role userRole = roleRepository.findByName(adminRequest.getRole());
        if  (userRole == null) {
            userRole = new Role(adminRequest.getRole());
            roleRepository.save(userRole);
        }

        User user = optUser.get();
        user.setRole(userRole);
        userRepository.save(user);
        return Mono.just(ResponseEntity.ok("Role has been granted to user!"));
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ResponseEntity<String>> addUser(@RequestBody AdminRequest adminRequest) {
        Optional<User> optUser = userRepository.findByUsername(adminRequest.getUsername());

        if (optUser.isPresent())
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("This username is already exists!"));

        Role userRole = roleRepository.findByName(adminRequest.getRole());
        if  (userRole == null) {
            userRole = new Role(adminRequest.getRole());
            roleRepository.save(userRole);
        }

        User user = new User(adminRequest.getUsername(), adminRequest.getPassword());
        user.setRole(userRole);
        userRepository.save(user);

        return Mono.just(ResponseEntity.ok("New user was created"));
    }
}