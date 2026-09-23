package com.elkased.hellowsl.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/// Response payload for a greeting (entity exposed to API clients).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A greeting stored in the in-memory database")
public class GreetingDto {

	@Schema(description = "Auto-generated id", example = "1")
	private Long id;

	@Schema(description = "The greeting message", example = "Hello from WSL!")
	private String message;

	@Schema(description = "Who created the greeting", example = "amirelkased")
	private String createdBy;

	@Schema(description = "Creation time in UTC (ISO-8601)", example = "2026-09-12T10:00:00Z")
	private Instant createdAt;

	/// Derived by the mapper (@AfterMapping): "<createdBy> - <message>".
	@Schema(description = "Derived label: <createdBy> - <message>", example = "amirelkased - Hello from WSL!")
	private String label;

}