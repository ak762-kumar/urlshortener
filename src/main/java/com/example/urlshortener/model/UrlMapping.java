package com.example.urlshortener.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

/**
 * This class represents the core data model for our URL Shortener. It is a POJO
 * (Plain Old Java Object), which means it's a simple object not bound by any
 * special restrictions or frameworks at this stage.
 *
 * Each instance of this class will eventually correspond to a single row in our
 * database table, holding the crucial link between an original, long URL and the
 * short code we generate for it.
 *
 * * The @Entity annotation is the most fundamental JPA annotation. It marks this
  * Java class as a manageable entity for the persistence framework (Hibernate).
  * This means Hibernate will be responsible for mapping instances of this class
  * to rows in a database table.
  *
  * By convention, Hibernate will create a table named after the class in snake_case,
  * so this entity will be mapped to a table named 'url_mapping'.
  */
@Entity // <-- THIS IS THE NEW ANNOTATION
public class UrlMapping {
// --- NEWLY ADDED FIELDS START HERE ---

    /**
     * The unique identifier for each URL mapping. This will serve as the Primary Key
     * in our database table. A primary key is a special column that uniquely
     * identifies each record (row) in a table.
     * We use the 'Long' wrapper class instead of the primitive 'long'. This allows the
     * id to be 'null' before the entity is first saved to the database. JPA
     * uses this null state to determine if an entity is new or already exists.
     */
        /**
     * The unique identifier for each URL mapping. This will serve as the Primary Key
     * in our database table.
     *
     * @Id: This annotation, from jakarta.persistence, explicitly marks this field
     *      as the primary key of the entity. Every entity MUST have a primary key.
     *
     * @GeneratedValue: This annotation specifies that the primary key value will be
     *                  generated automatically. We don't need to set it manually.
     *      strategy = GenerationType.IDENTITY: This strategy tells Hibernate to rely
     *      on an auto-incrementing column in the database. When we save a new entity,
     *      the database assigns the next available ID. This is a common and efficient
     *      strategy for many databases, including H2, MySQL, and PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The original, full-length URL that the user wants to shorten.
     *
     * @Lob: This annotation specifies that the field should be persisted as a
     *       Large Object. For a String field like this one, it tells Hibernate
     *       to use a database column type suitable for storing very long strings,
     *       such as CLOB (Character Large Object) or TEXT, instead of a standard
     *       VARCHAR which has a size limit. This makes our application robust
     *       against errors caused by exceptionally long URLs.
     */
    @Lob // <-- THIS IS THE NEW ANNOTATION
    private String originalUrl;

    /**
     * The generated unique short code that maps to the original URL.
     * This is the core part of our short link, for example, the 'xYz123' in
     * a URL like 'http://sho.rt/xYz123'.
     * * @Column(unique = true): This is a critical instruction for data integrity.
     * It tells the persistence provider (Hibernate) to generate a database schema
     * where the 'short_code' column has a UNIQUE constraint. This means the database
     * itself will enforce the rule that no two rows can have the same shortCode.
     * This is the ultimate safeguard against duplicate short links, which would
     * break the functionality of our application.
     */
    @Column(unique = true) // <-- THIS IS THE NEW ANNOTATION
    private String shortCode;

    /**
     * The timestamp indicating when this URL mapping was created.
     * We use java.time.LocalDateTime, which is the modern, standard Java API
     * for representing a date and time without a time zone. JPA has excellent
     * built-in support for persisting this type to an appropriate database column
     * (e.g., TIMESTAMP).
     */
    private LocalDateTime creationDate;

    /**
     * added test comments
     * A counter to track how many times the short link has been accessed or clicked.
     * We use the primitive 'long' type here. Unlike 'Long', a primitive 'long' cannot
     * be null and defaults to 0. This is perfect for a counter, as a link that
     * has never been clicked should have a count of 0, not null.
     */
    private long clickCount;

    // --- NEWLY ADDED FIELDS END HERE ---
}
