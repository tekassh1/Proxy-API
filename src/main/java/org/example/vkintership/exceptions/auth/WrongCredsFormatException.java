package org.example.vkintership.exceptions.auth;

public class WrongCredsFormatException extends Exception {
    public WrongCredsFormatException(String msg) {
        super(msg);
    }
}
