package com.elkased.hellowsl.controller;

import com.elkased.hellowsl.dto.CreateGreetingRequest;
import com.elkased.hellowsl.dto.GreetingDto;
import com.elkased.hellowsl.service.GreetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/// REST API for the Hello-WSL app.
@RestController
@RequestMapping("/api")
@Tag(name = "Greetings", description = "Greeting management REST API")
public class GreetingRestController {

	private final GreetingService greetingService;

	public GreetingRestController(GreetingService greetingService) {
		this.greetingService = greetingService;
	}

	@Operation(summary = "Say hello", description = "Returns a JSON greeting for the given name")
	@GetMapping("/hello")
	public Map<String, String> hello(@Parameter(description = "Name to greet",
			example = "WSL") @RequestParam(defaultValue = "WSL") String name) {
		return Map.of("greeting", "Hello, %s!".formatted(name), "backend", "Spring Boot on WSL", "database",
				"H2 in-memory");
	}

	@Operation(summary = "List all greetings", description = "Returns all greetings, newest first")
	@GetMapping("/greetings")
	public List<GreetingDto> listGreetings() {
		return greetingService.findAll();
	}

	@Operation(summary = "Get a greeting by id")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Greeting found"),
			@ApiResponse(responseCode = "404", description = "Greeting not found") })
	@GetMapping("/greetings/{id}")
	public ResponseEntity<GreetingDto> getGreeting(@PathVariable Long id) {
		return greetingService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Operation(summary = "Create a greeting")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Greeting created"),
			@ApiResponse(responseCode = "400", description = "Invalid payload") })
	@PostMapping("/greetings")
	public ResponseEntity<GreetingDto> createGreeting(@Valid @RequestBody CreateGreetingRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(greetingService.create(request));
	}

	@Operation(summary = "Delete a greeting by id")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Greeting deleted"),
			@ApiResponse(responseCode = "404", description = "Greeting not found") })
	@DeleteMapping("/greetings/{id}")
	public ResponseEntity<Void> deleteGreeting(@PathVariable Long id) {
		return greetingService.deleteById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

}