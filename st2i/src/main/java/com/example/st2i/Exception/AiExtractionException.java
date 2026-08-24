package com.example.st2i.Exception;

public class AiExtractionException extends RuntimeException {
    public AiExtractionException(String message) {
        super(message);
    }

    public AiExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
