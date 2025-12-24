// src/main/java/com/example/urlshortener/controller/PageController.java

package com.example.urlshortener.controller;

// NEW: Import the service we need to inject.
import com.example.urlshortener.service.UrlShortenerService;
// NEW: Import the Model, PostMapping, and RequestParam classes.
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
public class PageController {

    // --- NEWLY ADDED CODE START ---

    // We inject the UrlShortenerService using our preferred method: constructor injection.
    // This gives our PageController the ability to call the core business logic
    // for shortening a URL.
    private final UrlShortenerService urlShortenerService;

    public PageController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    // --- NEWLY ADDED CODE END ---

    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    // --- NEWLY ADDED METHOD START ---

    /**
     * This method handles the form submission from our index.html page.
     *
     * @PostMapping("/shorten-web"): This maps the method to handle POST requests sent
     *                               to the "/shorten-web" endpoint, matching our form's action.
     * @param longUrl This parameter is bound to the form's input data.
     *                The @RequestParam("longUrl") annotation tells Spring to find the value
     *                associated with the form input field that has name="longUrl" and
     *                assign it to this String variable.
     * @param model   The Model object is a map-like container provided by Spring. We will use
     *                it in the upcoming tasks to pass data (like the result of the shortening)
     *                from the controller back to the Thymeleaf view for rendering.
     * @return        The string "index", which tells Spring to re-render the index.html page.
     *                This way, the user stays on the same page to see the result.
     */
    @PostMapping("/shorten-web")
    public String handleShortenForm(@RequestParam("longUrl") String longUrl, Model model) {
        // This is the moment we connect the UI interaction to our core business logic.

        // 1. We call the shortenUrl method on our injected service instance.
        // 2. We pass the 'longUrl' variable, which contains the data submitted by the
        // user.
        // 3. The service executes all the steps to create and persist the URL mapping.
        // 4. Upon successful completion, the service returns the unique short code
        // (e.g., "aB1cDe").
        // 5. We store this returned code in a new local variable named 'shortCode'.
        String shortCode = urlShortenerService.shortenUrl(longUrl);

        // 1. Construct the full, clickable short URL.
        // The service provides the code, but the controller is responsible for knowing
        // the application's base URL and constructing the final link.
        String fullShortUrl = "http://localhost:8080/" + shortCode;

        // 2. Add the results to the Model object.
        // The Model acts as a container to pass data from the controller to the view.
        // Each attribute we add becomes a variable accessible within our Thymeleaf
        // template.

        // We add the user's original submission for their reference.
        // In index.html, we can now access this value using ${originalUrl}.
        model.addAttribute("originalUrl", longUrl);

        // We add the final result.
        // In index.html, we can now access this value using ${shortUrlResult}.
        model.addAttribute("shortUrlResult", fullShortUrl);
        // 3. Finally, we return "index" to re-render the same page.
        
        return "index";
    }
    // --- NEWLY ADDED METHOD END ---
}