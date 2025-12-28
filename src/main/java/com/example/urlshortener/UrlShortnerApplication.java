package com.example.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main entry point for the Spring Boot application.
 *
 * @SpringBootApplication: A convenience annotation that adds all of the
 *                         following:
 *                         - @Configuration: Tags the class as a source of bean
 *                         definitions.
 *                         - @EnableAutoConfiguration: Tells Spring Boot to
 *                         start adding beans based on
 *                         classpath settings, other beans, and various property
 *                         settings.
 *                         - @ComponentScan: Tells Spring to look for other
 *                         components, configurations,
 *                         and services in the 'com/example/urlshortener'
 *                         package, allowing it to find
 *                         our controllers, services, repositories, etc.
 *
 * @EnableScheduling: This is the master switch that activates Spring's
 *                    scheduled
 *                    task execution capabilities. Without this, any @Scheduled
 *                    annotations in the
 *                    application will be ignored. Spring will now discover and
 *                    run the
 *                    cleanupExpiredUrls() job in our CleanupService.
 */

@SpringBootApplication
@EnableScheduling
public class UrlShortnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortnerApplication.class, args);
	}

}
