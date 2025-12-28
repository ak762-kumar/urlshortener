package com.example.urlshortener.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

/**
 * A Data Transfer Object (DTO) that represents the request payload for
 * shortening a URL.
 * We use a Java Record here for its conciseness and immutability. Records
 * automatically
 * provide a constructor, getters for all fields, and proper equals(),
 * hashCode(), and
 * toString() methods.
 *
 * This record defines the JSON contract for our API's shorten endpoint. An
 * incoming
 * request body must be a JSON object with a key named "url".
 * For example:
 * {
 * "url": "https://www.verylongurlto-make-short.com/with/lots/of/params"
 * }
 *
 * @param url           The original, long URL that the user wants to shorten.
 *                      We add validation annotations to ensure the URL is not
 *                      empty and is well-formed.
 * @param customAlias   An optional user-defined alias for the short URL.
 * @param hoursToExpire An OPTIONAL time-to-live (TTL) in hours. If provided, the link will expire after this many hours. If null, the link is permanent.
 */
public record ShortenUrlRequest(
        // The @NotEmpty annotation ensures that the provided URL string is not null and
        // not empty.
        @NotEmpty(message = "URL cannot be empty")
        // The @URL annotation from Hibernate Validator provides a robust check to
        // ensure
        // the string is a validly formatted URL.
        @URL(message = "A valid URL format is required") String url,
        String CustomAlias,
        /**
        * The time-to-live for the URL in hours.
        * - We use the 'Integer' wrapper class instead of the primitive 'int' so that
        * the value can be 'null'. A null value indicates that the user did not
        * specify an expiration, meaning the link should be permanent.
        * - @Min(1): This validation annotation ensures that if a value IS provided,
        * it must be a positive integer (1 or greater). This prevents non-sensical
        * values like 0 or -5. Spring's validation mechanism will automatically
        * check this and reject the request with a 400 Bad Request if the rule is
        * violated.
                 */
        @Min(value = 1, message = "Hours to expire must be a positive number")
        Integer hoursToExpire) {
}