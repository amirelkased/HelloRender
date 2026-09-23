package com.elkased.hellowsl.config;

import com.elkased.hellowsl.dto.CreateGreetingRequest;
import com.elkased.hellowsl.repository.GreetingRepository;
import com.elkased.hellowsl.service.GreetingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// Seeds the in-memory database with sample greetings on startup.
@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedGreetings(GreetingService greetingService, GreetingRepository greetingRepository) {
		return args -> {
			if (greetingRepository.count() == 0) {
				greetingService.create(new CreateGreetingRequest("Hello from Spring Boot on WSL!", "amirelkased"));
				greetingService.create(new CreateGreetingRequest("Hello from an in-memory H2 database!", "spring"));
				greetingService
					.create(new CreateGreetingRequest("Hello, world! Your starter app is running.", "docker"));
			}
		};
	}

}