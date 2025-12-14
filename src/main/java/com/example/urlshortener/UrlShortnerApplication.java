package com.example.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//this annotation will add @Configuration -> tags class as a source bean defination for application context
//@EnableAutoCOnfiguration -> Tells SprigBoot to start adding beans based on classpath setting, on other beans, and various propert settings.This is the "magic" that automatically configures Tomcat,H2, and other libraries for us
//@ComponentScan -> Tells Spring to scan for other components, configurations, and services in the same package (and sub-packages), allowing it to find our HealthCheckController.

@SpringBootApplication
public class UrlShortnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortnerApplication.class, args);
	}

}
