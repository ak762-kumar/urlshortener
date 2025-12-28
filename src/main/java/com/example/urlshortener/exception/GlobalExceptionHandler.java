

package com.example.urlshortener.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {



    /**
     * This method is a dedicated handler for the UrlNotFoundException.
     *
     * @ExceptionHandler(UrlNotFoundException.class): This annotation registers this
     * method
     * as the specific handler for any UrlNotFoundException thrown by any
     * controller.
     *
     * @param ex      The actual exception object that was thrown. Spring injects
     *                this for us,
     *                so we can use its properties, like the error message.
     * @param request The web request during which the exception occurred. We can
     *                use this
     *                to get more context if needed.
     * @return A ResponseEntity that encapsulates the HTTP response. We build a
     *         structured
     *         response with a 404 status code and a JSON body containing error
     *         details.
     */
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Object> handleUrlNotFoundException(UrlNotFoundException ex, WebRequest request) {
        // Create a structured, map-based body for our JSON response.
        // Using a LinkedHashMap preserves the insertion order of the keys.        Map<String, Object> body = new LinkedHashMap<>();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value()); 
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); 
        body.put("path", request.getDescription(false).replace("uri=", "")); 
//pass both longUrl and customAlias(potentially null) to the service            



        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
//If the service throws AliasAlreadyExistsException, add an error message to the model                }
//we add a user friendly error message to the model for Thymleaf to display            

    
    @ExceptionHandler(AliasAlreadyExistsException.class)
    public ResponseEntity<Object> handleAliasAlreadyExistsException(AliasAlreadyExistsException ex, WebRequest request) {
        
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); 
        body.put("error", "Conflict"); 
        body.put("message", ex.getMessage()); 
        body.put("path", request.getDescription(false).replace("uri=", ""));

        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}