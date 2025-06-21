// src/main/java/br/unialfa/hackathon/exception/ApiExceptionHandler.java
package br.unialfa.hackathon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        // Aqui você pode adicionar lógica mais granular para diferentes tipos de RuntimeException
        // Por exemplo, para "Credenciais inválidas."
        if (ex.getMessage().contains("Credenciais inválidas")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("mensagem", ex.getMessage()));
        }
        // Para outras RuntimeExceptions genéricas, pode retornar um Internal Server Error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("mensagem", "Ocorreu um erro interno: " + ex.getMessage()));
    }

    // Você pode adicionar mais @ExceptionHandler para outras exceções específicas
    // Por exemplo, para recursos não encontrados:
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("mensagem", ex.getMessage()));
    }

    // ... outros handlers para validação, etc.
}