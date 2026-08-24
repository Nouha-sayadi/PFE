package com.example.st2i.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)          // 400
                .body(errors);
    }

    @ExceptionHandler({FileTooLargeException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<Map<String, String>> handleFileTooLarge(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)    // 413
                .body(Map.of("message", "Le fichier dépasse la taille maximale autorisée (10 Mo)."));
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFileType(InvalidFileTypeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)          // 400
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDocumentNotFound(DocumentNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)            // 404
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(OcrExtractionException.class)
    public ResponseEntity<Map<String, String>> handleOcrExtraction(OcrExtractionException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY) // 422
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(AiExtractionException.class)
    public ResponseEntity<Map<String, String>> handleAiExtraction(AiExtractionException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY) // 422
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)          // 400
                .body(Map.of("message", ex.getMessage()));
    }
}