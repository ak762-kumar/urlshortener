// We are still in the 'service' package we created in the last task.
package com.example.urlshortener.service;

import java.time.LocalDateTime;
import java.util.Optional;

// NEW: We must import the @Service annotation. This makes it available to our class.
// The 'stereotype' package contains annotations that define the roles of beans.
import org.springframework.stereotype.Service;

import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;

import jakarta.transaction.Transactional;

/**
 * This class represents the core business logic layer for our URL shortener.
 * 
 * The @Service annotation marks this class as a Spring service component.
 * It's a specialized form of the @Component annotation, used to indicate that
 * it's holding business logic. As a Spring-managed bean, an instance of this
 * class will be created by the Spring container and can be injected into
 * other components (like our controllers).
 *
 * This service will be responsible for orchestrating the creation of short
 * URLs,
 * which involves interacting with the repository to persist URL mappings.
 */
@Service // <-- THIS IS THE NEW ANNOTATION
public class UrlShortenerService {
    /**
     * This field will hold the reference to our repository.
     * 'private': It's an internal detail of this service, so we keep it private.
     * 'final': We declare it as 'final' because it's a required dependency that
     * will not change after the service is created. This is a best practice
     * for immutability and thread safety.
     */
    private final UrlMappingRepository urlMappingRepository;

    // A constant holding all the characters for our base-62 encoding.
    // It is 'static' and 'final' because it's a constant value that never changes
    // and is shared across all instances of this service.
    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * This is the constructor for our service. This is where Dependency Injection
     * happens.
     * When Spring creates the UrlShortenerService bean, it will see this
     * constructor
     * and look for a bean of type UrlMappingRepository in its context.
     *
     * Since Spring Data JPA automatically created an implementation for our
     * repository
     * interface, Spring finds it and "injects" or passes it into this constructor.
     *
     * NOTE: As of Spring 4.3, if a class has only one constructor, the @Autowired
     * annotation is no longer required. Spring is smart enough to know that it
     * should use this constructor for autowiring. We follow this modern convention.
     *
     * @param urlMappingRepository The instance of UrlMappingRepository provided by
     *                             Spring.
     */
    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        // We assign the injected repository to our final field.
        this.urlMappingRepository = urlMappingRepository;
    }

    /**
     * The core business logic for creating a short URL.
     * This method will orchestrate the entire process:
     * 1. Create a UrlMapping entity.
     * 2. Save it to the database to generate a unique primary key (ID).
     * 3. Convert this ID into a unique base-62 short code.
     * 4. Update the entity with the new short code.
     * 5. Return the generated short code.
     *
     * @param originalUrl The long URL that needs to be shortened. This is the input
     *                    from the user.
     * @return The generated unique short code (e.g., "aB1cDe") corresponding to the
     *         originalUrl.
     */

    // By default, repository methods are transactional. However, our method orchestrates multiple database operations. Wrapping it in @Transactional ensures that these operations are executed as a single, atomic unit. If any part fails, all previous operations in the method are rolled back.
    @Transactional
    public String shortenUrl(String originalUrl, String customAlias) {
        // Step 1: Create a new UrlMapping entity instance. This object will represent the new row we want to save in our database.
        UrlMapping urlMapping = new UrlMapping();
        // Step 2: Set the properties we already know. We set the originalUrl & CreationDate from the method parameter.
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setCreationDate(LocalDateTime.now());
        // The 'shortCode' field is deliberately left null for now.

        // Step 3: Save the entity to the database. This is the crucial step.
        // We call the .save() method provided by our JpaRepository.
        // When save() is called on an entity with a null ID, JPA performs an INSERT operation.
        // The database's auto-increment feature generates a unique primary key (ID).
        // The save() method is transactional and returns the fully persisted entity, which now includes the database-generated ID.
        UrlMapping savedEntity = urlMappingRepository.save(urlMapping);

        // Now, we take the unique ID from the saved entity and convert it
        // into our base-62 short code.
        String shortCode = encodeBase62(savedEntity.getId());

        // 4. Update the entity with the generated code ->
        // We are now updating our managed entity instance with the generated short code.
        // The 'setShortCode()' method was automatically generated for us by Lombok's @Setter annotation.
        // By calling this method, we are changing the state of the 'savedEntity' object in memory.
        // JPA's Persistence Context will detect this change (a process called dirty checking).
        savedEntity.setShortCode(shortCode);

        // 5.Second save to update the record with the new short code
        // Because savedEntity now has a non-null ID, JPA knows to perform an UPDATE operation instead of an INSERT. This call persists the shortCode to the database, completing the record.
        //NOTE: Dirty Checking would automatically handle this update when the transaction commits, so this explicit save() call is optional in this specific case. However, including it here makes the operation explicit and clear.
        urlMappingRepository.save(savedEntity);

        // 6. Fulfill the method's contract by returning the generated short code.
        // This value is what the caller (e.g., our future API controller) will receive and use to construct the final, shareable short URL for the user.
        return shortCode;
    }

    /**
     * Finds the original URL for a given short code and increments its click count.
     * The @Transactional annotation ensures that the find,increment, and save operations
     * are performed as a single atomic unit. If any part fails, all changes are rolled back to maintain data integrity.
     *
     * @param shortCode The unique code representing the shortened URL.
     * @return The original, long URL to redirect to.
     * @throws // In a later step, this will throw a custom UrlNotFoundException if
     */
    @Transactional
    public String getOriginalUrlAndIncrementClicks(String shortCode) {
        // The .orElseThrow() method is the most elegant way to handle an Optional that
        // is expected to contain a value.
        // It attempts to get the value from the Optional. If the Optional is empty,
        // it throws the exception provided by the Supplier lambda `() -> ...`.
        // This single line replaces the entire if/else block.
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));

        // This part of the code is only reached if a UrlMapping was found.
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);

        // Within a @Transactional method, this save call is technically optional due to
        // dirty checking, but it makes the intent to persist the change explicit.
        urlMappingRepository.save(urlMapping);

        return urlMapping.getOriginalUrl();
    }

    /**
     * Retrieves statistics for a given short code.
     * This is a read-only operation and doesn't need to be @Transactional by itself,
     * but adding it is harmless and keeps it consistent with other data-access methods.
     *
     * @param shortCode The unique code to look up.
     * @return A UrlStatsResponse DTO containing the statistics.
     * @throws UrlNotFoundException if the short code does not exist.
     */
    public UrlStatsResponse getStats(String shortCode) {
        // Step 1: Find the entity. We reuse our repository's custom find method.
        // Step 2: Validate. We reuse the .orElseThrow() pattern with our existing
        // custom exception. This ensures our API's error handling is consistent.
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No statistics found for short code: " + shortCode));

        // Step 3: Transform (Map) the entity to the DTO.
        // We construct the full URL here for user convenience, as the DTO contract requires it.
        String fullShortUrl = "http://localhost:8080/" + urlMapping.getShortCode();
        
        // We create a new instance of our immutable UrlStatsResponse record,
        // populating it with data from the UrlMapping entity we just fetched.
        return new UrlStatsResponse(
            urlMapping.getOriginalUrl(),
            fullShortUrl,
            urlMapping.getCreationDate(),
            urlMapping.getClickCount()
        );
    }
    /**
     * A private utility method to convert a base-10 number (our database ID)
     * into a base-62 string.
     * Base-62 uses characters 0-9, a-z, A-Z, making it URL-safe and compact.
     *
     * @param number The unique ID of the URL mapping (a positive long).
     * @return The base-62 encoded string.
     */
    private String encodeBase62(Long number) {
        // If the number is 0, we return the first character of our character set.
        if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        // StringBuilder is highly efficient for concatenating strings in a loop.
        StringBuilder sb = new StringBuilder();
        long num = number; // Use a mutable copy of the number for our calculations.

        // This is the core base conversion algorithm.
        while (num > 0) {
            // The modulo operator (%) gives the remainder of a division.
            // This remainder is our index into the BASE62_CHARS string.
            int remainder = (int) (num % 62);

            // Append the character corresponding to the remainder to our result.
            sb.append(BASE62_CHARS.charAt(remainder));

            // Perform integer division by 62 to prepare for the next iteration.
            num /= 62;
        }
        // The algorithm builds the string in reverse order (from least significant
        // "digit" to most significant). We must reverse it before returning.
        // For example, the number 62 would produce "01" during the loop, which
        // needs to be reversed to the correct "10" representation.
        return sb.reverse().toString();
    }

}