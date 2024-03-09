package org.example.vkintership.exceptions.auth;

public class UserDoesntExistException extends Exception {
    public UserDoesntExistException(String msg) {
        super(msg);
    }
}