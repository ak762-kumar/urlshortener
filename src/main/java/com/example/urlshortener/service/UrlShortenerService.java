// We are still in the 'service' package we created in the last task.
package com.example.urlshortener.service;

// NEW: We must import the @Service annotation. This makes it available to our class.
// The 'stereotype' package contains annotations that define the roles of beans.
import org.springframework.stereotype.Service;

/**
 * This class represents the core business logic layer for our URL shortener.
 *
 * The @Service annotation marks this class as a Spring service component.
 * It's a specialized form of the @Component annotation, used to indicate that
 * it's holding business logic. As a Spring-managed bean, an instance of this
 * class will be created by the Spring container and can be injected into
 * other components (like our controllers).
 *
 * This service will be responsible for orchestrating the creation of short URLs,
 * which involves interacting with the repository to persist URL mappings.
 */
@Service // <-- THIS IS THE NEW ANNOTATION
public class UrlShortenerService {
    // This class is still a shell. Now that it is a managed Spring Bean, our
    // next step will be to give it the tools it needs to do its job by
    // injecting the UrlMappingRepository.
}