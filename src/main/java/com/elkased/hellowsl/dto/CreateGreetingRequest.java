package com.elkased.hellowsl.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/// Payload to create a greeting via the REST API or web form.
@Schema(description = "Payload to create a greeting")
public record CreateGreetingRequest(

		@NotBlank @Schema(description = "The greeting message", example = "Hello from WSL!",
				requiredMode = Schema.RequiredMode.REQUIRED) String message,

		@NotBlank @Schema(description = "Name of who creates it", example = "amirelkased",
				requiredMode = Schema.RequiredMode.REQUIRED) String createdBy) {
}