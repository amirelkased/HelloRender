package com.elkased.hellowsl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/// End-to-end tests against a real embedded server, grouped by feature.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloWslApplicationTests {

	@LocalServerPort
	private int port;

	private RestClient rest() {
		return RestClient.builder().baseUrl("http://localhost:" + port).build();
	}

	/// GET that returns even for 4xx/5xx (default handlers would throw).
	private ResponseEntity<Map> softGet(String uriTemplate, Long id) {
		return rest().get().uri(uriTemplate, id).retrieve().onStatus(HttpStatusCode::isError, (req, res) -> {
		}).toEntity(Map.class);
	}

	/// DELETE that returns even for 4xx/5xx (default handlers would throw).
	private ResponseEntity<Void> softDelete(String uriTemplate, Long id) {
		return rest().delete().uri(uriTemplate, id).retrieve().onStatus(HttpStatusCode::isError, (req, res) -> {
		}).toBodilessEntity();
	}

	/// POST that returns even for 4xx/5xx (default handlers would throw).
	private ResponseEntity<Map> softPost(Object body) {
		return rest().post()
			.uri("/api/greetings")
			.contentType(MediaType.APPLICATION_JSON)
			.body(body)
			.retrieve()
			.onStatus(HttpStatusCode::isError, (req, res) -> {
			})
			.toEntity(Map.class);
	}

	@Nested
	@DisplayName("Home page")
	class HomePage {

		@Test
		void loads() {
			ResponseEntity<String> response = rest().get().uri("/").retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).contains("Hello-WSL");
		}

	}

	@Nested
	@DisplayName("GET /api/hello")
	class Hello {

		@Test
		void greetsDefaultName() {
			ResponseEntity<Map> response = rest().get().uri("/api/hello").retrieve().toEntity(Map.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).containsEntry("greeting", "Hello, WSL!");
		}

		@Test
		void greetsCustomName() {
			ResponseEntity<Map> response = rest().get().uri("/api/hello?name=Amir").retrieve().toEntity(Map.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).containsEntry("greeting", "Hello, Amir!");
		}

	}

	@Nested
	@DisplayName("GET /api/greetings")
	class ListGreetings {

		@Test
		void returnsSeededGreetings() {
			ResponseEntity<List<Map<String, Object>>> response = rest().get()
				.uri("/api/greetings")
				.retrieve()
				.toEntity(new ParameterizedTypeReference<>() {
				});
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).isNotEmpty()
				.anySatisfy(g -> assertThat(g).containsEntry("message", "Hello from Spring Boot on WSL!"))
				.anySatisfy(g -> assertThat(g).containsEntry("message", "Hello from an in-memory H2 database!"))
				.anySatisfy(g -> assertThat(g).containsEntry("message", "Hello, world! Your starter app is running."));
		}

	}

	@Nested
	@DisplayName("GET /api/greetings/{id}")
	class GetGreeting {

		@Test
		void returnsGreetingWhenExists() {
			ResponseEntity<Map> response = rest().get().uri("/api/greetings/1").retrieve().toEntity(Map.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).containsEntry("message", "Hello from Spring Boot on WSL!");
		}

		@Test
		void returnsNotFoundWhenMissing() {
			ResponseEntity<Map> response = softGet("/api/greetings/{id}", 9999L);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

	}

	@Nested
	@DisplayName("POST /api/greetings")
	class CreateGreeting {

		@Test
		void createsAndReturnsCreatedGreeting() {
			ResponseEntity<Map> response = rest().post()
				.uri("/api/greetings")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Map.of("message", "Hello from a test!", "createdBy", "junit"))
				.retrieve()
				.toEntity(Map.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getBody()).containsEntry("message", "Hello from a test!");
		}

		@Test
		void rejectsBlankMessage() {
			ResponseEntity<Map> response = softPost(Map.of("message", "  ", "createdBy", "junit"));

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}

		@Test
		void rejectsMissingFields() {
			ResponseEntity<Map> response = softPost("{}");

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}

	}

	@Nested
	@DisplayName("DELETE /api/greetings/{id}")
	class DeleteGreeting {

		@Test
		void deletesExistingGreetingThenNotFound() {
			ResponseEntity<Map> created = rest().post()
				.uri("/api/greetings")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Map.of("message", "To be deleted", "createdBy", "cleanup"))
				.retrieve()
				.toEntity(Map.class);
			assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			Number id = (Number) created.getBody().get("id");

			ResponseEntity<Void> deleted = softDelete("/api/greetings/{id}", id.longValue());
			assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

			ResponseEntity<Map> missing = softGet("/api/greetings/{id}", id.longValue());
			assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

		@Test
		void returnsNotFoundWhenAlreadyMissing() {
			ResponseEntity<Void> response = softDelete("/api/greetings/{id}", 9999L);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

	}

	@Nested
	@DisplayName("API docs")
	class ApiDocs {

		@Test
		void servesOpenApiSpec() {
			ResponseEntity<String> response = rest().get().uri("/v3/api-docs").retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).contains("Greetings");
		}

		@Test
		void servesSwaggerUi() {
			ResponseEntity<String> redirect = rest().get().uri("/swagger-ui.html").retrieve().toEntity(String.class);
			assertThat(redirect.getStatusCode()).isEqualTo(HttpStatus.FOUND);

			ResponseEntity<String> response = rest().get()
				.uri("/swagger-ui/index.html")
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		}

		@Test
		void servesScalarUi() {
			ResponseEntity<String> response = rest().get().uri("/scalar").retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		}

	}

}