package org.example.vkintership.service;

import jakarta.annotation.PostConstruct;
import org.example.vkintership.entity.Role;
import org.example.vkintership.entity.User;
import org.example.vkintership.exceptions.auth.UsernameExistsException;
import org.example.vkintership.exceptions.auth.WrongCredsFormatException;
import org.example.vkintership.repository.RoleRepository;
import org.example.vkintership.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.username}") String adminUsername;
    @Value("${admin.password}") String adminPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty())
            throw new UsernameNotFoundException("Wrong username");
        return user.get();
    }

    @PostConstruct
    private void createAdmin() {

        if (userRepository.findByUsername("admin").isPresent()) return;

        User user = new User(adminUsername, passwordEncoder.encode(adminPassword));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }

        user.setRoles(Collections.singleton(adminRole));
        userRepository.save(user);
    }

    public void registerUser(User user) throws UsernameExistsException, WrongCredsFormatException {
        if (!validateUsername(user.getUsername()))
            throw new WrongCredsFormatException("Username should contain only letters and numbers (5-15 symbols)");
        if (!validatePassword(user.getPassword()))
            throw new WrongCredsFormatException("Password should contain at least one letter and digit " +
                    "(5-15 symbols, no spaces)");

        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UsernameExistsException("This username is already exists!");

        Role defaultRole = roleRepository.findByName("ROLE_USER");

        if (defaultRole == null) {
            defaultRole = new Role("ROLE_USER");
            roleRepository.save(defaultRole);
        }
        user.setRoles(Collections.singleton(defaultRole));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private boolean validateUsername(String username) {
        Pattern pattern = Pattern.compile("^[0-9A-Za-z_]{5,15}$");
        return pattern.matcher(username).matches();
    }

    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("^(?!.* )(?=.*?\\d)(?=.*?[a-zA-Z])[a-zA-Z\\d]+.{5,15}$");
        return pattern.matcher(password).matches();
    }
}