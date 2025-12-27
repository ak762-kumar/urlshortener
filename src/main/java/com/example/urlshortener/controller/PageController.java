// src/main/java/com/example/urlshortener/controller/PageController.java

package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.exception.AliasAlreadyExistsException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.service.UrlShortenerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final UrlShortenerService urlShortenerService;

    public PageController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    @PostMapping("/shorten-web")
     public String handleShortenForm(
            @RequestParam("longUrl") String longUrl,
            // NEW: Add a parameter for the custom alias.
            // The `name` must match the input's 'name' attribute in the HTML.
            // `required = false` tells Spring that this parameter is optional.
            // If the user doesn't submit a value, 'customAlias' will be null.
            @RequestParam(name = "customAlias", required = false) String customAlias,
            Model model
    ) {
        // We add the original URL to the model immediately, so the user's input
        // is preserved on the page even if there's an error.
        model.addAttribute("originalUrl", longUrl);

        try {
            // UPDATED: Pass both the URL and the (potentially null) alias to the service.
            String shortCode = urlShortenerService.shortenUrl(longUrl, customAlias);
            String fullShortUrl = "http://localhost:8080/" + shortCode;

            // Add the successful result to the model.
            model.addAttribute("shortUrlResult", fullShortUrl);

        } catch (AliasAlreadyExistsException e) {
            // If the service throws our custom exception, we catch it.
            // We add a user-friendly error message to the model for Thymeleaf to display.
            model.addAttribute("aliasError", e.getMessage());
        }
        
        return "index";
    }

    /**
     * Handles the form submission for checking URL statistics.
     *
     * @PostMapping("/check-stats"): Maps POST requests from our new stats form to
     * this method.
     * 
     * @param shortCode The @RequestParam("checkShortCode") annotation tells Spring
     *                  to find the
     *                  form data with the key "checkShortCode" (matching our
     *                  input's 'name' attribute)
     *                  and inject its value into this String parameter.
     * @param model     The Model object, which we will use in the next task to pass
     *                  the
     *                  retrieved statistics back to the view for rendering.
     * @return The string "index", telling Spring to re-render the index.html page
     *         to display the results.
     */
    @PostMapping("/check-stats")
    public String handleStatsCheckForm(@RequestParam("checkShortCode") String shortCode, Model model) {
        try {
            // 1. Call the service to get the statistics. If successful, this returns our DTO.
            UrlStatsResponse stats = urlShortenerService.getStats(shortCode);
            // 2. Add the successfully retrieved stats object to the model.
            // We will use the key "urlStats" to reference this object in our HTML.
            model.addAttribute("urlStats", stats);
        } catch (UrlNotFoundException e) {
            // 3. If the service throws UrlNotFoundException, we catch it here.
            // This prevents the application from showing a generic error page.
            // Instead, we add a user-friendly error message to the model.
            // We will use the key "statsError" to check for this message in our HTML.
            model.addAttribute("statsError", "Statistics not found for short code: " + shortCode);
        }
        return "index";
    }

}