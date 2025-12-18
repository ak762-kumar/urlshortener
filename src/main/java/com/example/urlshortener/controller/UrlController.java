// src/main/java/com/example/urlshortener/controller/UrlController.java

package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenUrlRequest;
import com.example.urlshortener.dto.ShortenUrlResponse;
import com.example.urlshortener.service.UrlShortenerService;
// NEW: Import the @Valid annotation for triggering validation
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
// NEW: Import the necessary HTTP and web-related classes
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    // This is the method we created in the previous task.
    // Now we will add the parameters to its signature.
    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        
        // Step 1: Delegate to the service to get the unique code.
        String shortCode = urlShortenerService.shortenUrl(request.url());

        // Step 2: Construct the full, user-facing URL.
        String fullShortUrl = "http://localhost:8080/" + shortCode;

        // --- HIGHLIGHTED CHANGE START ---

        // Step 3: Package the result into our response DTO.
        // We use the 'new' keyword with our record, which calls its "canonical constructor"
        // that the Java compiler generated for us automatically.
        ShortenUrlResponse response = new ShortenUrlResponse(fullShortUrl);

        // Step 4: Return a complete, professional HTTP response.
        // We use the ResponseEntity builder pattern to construct the response.
        // - .status(HttpStatus.CREATED): Sets the HTTP status code to 201 Created. This is the
        //   semantically correct code for a successful POST request that creates a new resource.
        // - .body(response): Sets our response DTO as the body of the HTTP response. Spring will
        //   automatically serialize this Java object into a JSON string.
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}