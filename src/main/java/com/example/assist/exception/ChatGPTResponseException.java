package com.example.assist.exception;

public class ChatGPTResponseException extends RuntimeException {
    public ChatGPTResponseException(String message) {
        super(message);
    }
}
