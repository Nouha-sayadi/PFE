package com.example.st2i.Exception;

public class OcrExtractionException extends RuntimeException {
    public OcrExtractionException(String message) {
        super(message);
    }

    public OcrExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
