// This line declares that our new interface belongs to the 'repository' package.
// Organizing our code by layer (controller, model, repository, service) is a key
// principle for building maintainable and scalable applications.
package com.example.urlshortener.repository;

// NEW: We import Optional, a container object which may or may not contain a non-null value.
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.urlshortener.model.UrlMapping;

/**
 * This interface will serve as our Data Access Layer for the UrlMapping entity.
 * In Java, an interface is a contract that defines a set of method signatures
 * without providing their implementation.
 *
 * This is the perfect foundation for Spring Data JPA. By following specific
 * conventions (which we will do in the next step), Spring will automatically
* generate a concrete implementation of this interface at runtime. This generated
 * class will contain all the necessary code to perform database operations
 * (like save, find, delete) on our UrlMapping entities.
 *
 * This powerful feature eliminates the need for writing boilerplate DAO (Data Access Object)
 * code, allowing us to focus on our application's business logic.
 *  * Spring Data JPA will automatically create a proxy implementation of this
 * interface at runtime. This implementation can then be injected into other
* Spring components, like our service classes.
 *
 * The generic parameters <UrlMapping, Long> specify that this repository is
 * for managing 'UrlMapping' entities, and the type of the entity's
 * primary key is 'Long'.

 */
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    
    /**
     * This is a custom derived query method. Spring Data JPA will automatically
     * generate a query based on the method name.
     *
     * How it works:
     * - "find": This is the introductory keyword.
     * - "By": This keyword separates the find operation from the criteria.
     * - "ShortCode": This is the property name in our UrlMapping entity. Spring Data JPA
     *                parses this and understands it needs to create a query that filters
     *                by the 'shortCode' field. The field name must match exactly.
     *
     * The generated SQL query will be equivalent to:
     * "SELECT * FROM url_mapping WHERE short_code = ?"
     *
     * The method returns an Optional<UrlMapping>, which is a modern, robust way
     * to handle cases where a result may or may not be found, preventing NullPointerExceptions.
     *
     * @param shortCode The short code to search for in the database.
     * @return An Optional containing the UrlMapping if found, or an empty Optional otherwise.
     * By returning Optional<UrlMapping>, our repository communicates a clear contract: "I will try to find a UrlMapping, but I might not find one, and you need to be prepared for that possibility." This leads to safer, cleaner, and more readable code in our service layer.
     */
    Optional<UrlMapping> findByShortCode(String shortCode);
}