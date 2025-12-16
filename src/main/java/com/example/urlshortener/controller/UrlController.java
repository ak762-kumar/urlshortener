// src/main/java/com/example/urlshortener/controller/UrlController.java

package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenUrlRequest;
import com.example.urlshortener.dto.ShortenUrlResponse;
import com.example.urlshortener.service.UrlShortenerService;
// NEW: Import the @Valid annotation for triggering validation
import jakarta.validation.Valid;
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
    // --- HIGHLIGHTED CHANGE START ---
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        // --- HIGHLIGHTED CHANGE END ---

        // The body of the method is still empty. We've only defined how it receives
        // data.
        // In the next tasks, we will implement the logic here, calling the service
        // and returning a proper response.
        return null; // Temporary placeholder to satisfy the compiler.
    }
}