package ru.practicum.shareit.exceptions;

public class PaginationInvalidParamException extends RuntimeException {
    public PaginationInvalidParamException(String message) {
        super(message);
    }
}