package org.example.vkintership.exceptions.auth;

public class UsernameExistsException extends Exception {
    public UsernameExistsException(String msg) {
        super(msg);
    }
}