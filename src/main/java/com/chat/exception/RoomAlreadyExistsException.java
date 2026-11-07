package com.chat.exception;

public class RoomAlreadyExistsException extends RuntimeException {
    public RoomAlreadyExistsException(String name) {
        super("Room already exists with name: " + name);
    }
}
