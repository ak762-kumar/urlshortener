// src/main/java/com/example/urlshortener/service/CleanupService.java

package com.example.urlshortener.service;

import com.example.urlshortener.repository.UrlMappingRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * A service dedicated to performing background, scheduled tasks for the
 * application.
 *
 * This service is responsible for system maintenance duties, such as cleaning
 * up
 * expired data. Creating a separate service for this adheres to the Separation
 * of Concerns principle, keeping the user-facing logic in UrlShortenerService
 * separate from the internal, automated logic here.
 *
 * @Service: This annotation marks the class as a Spring-managed service bean.
 *           Spring's component scanner will detect it and register it in the
 *           application
 *           context, making it eligible for features like dependency injection
 *           and scheduling.
 */
@Service
public class CleanupService {

    // It's a best practice to include a logger in services that perform background
    // tasks. This allows us to log information about when the task runs and what it
    // does,
    // which is invaluable for debugging and monitoring in a production environment.
    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    // The CleanupService will need to interact with the database to find and delete
    // expired links. Therefore, we inject the UrlMappingRepository.
    private final UrlMappingRepository urlMappingRepository;

    /**
     * Constructor-based dependency injection. Spring will automatically provide an
     * instance
     * of UrlMappingRepository when it creates the CleanupService bean.
     *
     * @param urlMappingRepository The repository for accessing URL mapping data.
     */
    public CleanupService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    /**
     * A scheduled job that runs automatically to clean up expired URL mappings.
     *
     * @Scheduled: This annotation marks the method as a scheduled task.
     *   - cron = "0 0 1 * * ?": This cron expression configures the task to run
     *     every day at 1:00 AM.
     *     - Second: 0
     *     - Minute: 0
     *     - Hour: 1
     *     - Day of Month: * (any)
     *     - Month: * (any)
     *     - Day of Week: ? (not specified)
     *
     * This method will be executed by Spring's task scheduler in a background thread.
    */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional  // Ensures that the delete operation is executed within a transaction.
    public void cleanupExpiredUrls() {
        // We use the logger we set up earlier to provide visibility into the job's execution.
        // This is crucial for monitoring and debugging in a production environment.
        logger.info("Starting scheduled job: Cleaning up expired URL mappings...");

        // 1. Get the current time. This will be the reference point for what is considered "expired".
        LocalDateTime now = LocalDateTime.now();

        // 2. Call our new, efficient repository method.
        // This single line executes a bulk DELETE command on the database.
        long deletedCount = urlMappingRepository.deleteByExpirationDateBefore(now);
        
        // 3. Log the result. This provides crucial visibility into what the automated
        // job did. In a production system, this log is essential for monitoring.
        if (deletedCount > 0) {
            logger.info("Finished scheduled job: Successfully deleted {} expired URL mappings.", deletedCount);
        } else {
            logger.info("Finished scheduled job: No expired URL mappings found to delete.");
        }
    }
}