package com.elkased.hellowsl.mapper;

import com.elkased.hellowsl.dto.CreateGreetingRequest;
import com.elkased.hellowsl.dto.GreetingDto;
import com.elkased.hellowsl.entity.Greeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// Exercises the mapper, in particular that the @AfterMapping label logic runs
/// even though the mapping target is a Lombok class with a builder.
@DisplayName("GreetingMapper")
class GreetingMapperTest {

	private GreetingMapper mapper;

	private Greeting sampleEntity() {
		Greeting greeting = new Greeting();
		greeting.setId(7L);
		greeting.setMessage("Hello from a test!");
		greeting.setCreatedBy("junit");
		greeting.setCreatedAt(Instant.parse("2026-09-12T10:00:00Z"));
		return greeting;
	}

	@BeforeEach
	void setUp() {
		mapper = Mappers.getMapper(GreetingMapper.class);
	}

	@Nested
	@DisplayName("toDto")
	class ToDto {

		@Test
		void mapsPlainFields() {
			GreetingDto dto = mapper.toDto(sampleEntity());
			assertThat(dto.getId()).isEqualTo(7L);
			assertThat(dto.getMessage()).isEqualTo("Hello from a test!");
			assertThat(dto.getCreatedBy()).isEqualTo("junit");
			assertThat(dto.getCreatedAt()).isEqualTo(Instant.parse("2026-09-12T10:00:00Z"));
		}

		@Test
		void appliesAfterMappingLabel() {
			assertThat(mapper.toDto(sampleEntity()).getLabel()).isEqualTo("junit - Hello from a test!");
		}

		@Test
		void returnsNullForNullSource() {
			assertThat(mapper.toDto(null)).isNull();
		}

	}

	@Nested
	@DisplayName("toDtos")
	class ToDtos {

		@Test
		void mapsEveryElementWithLabel() {
			Greeting first = sampleEntity();
			Greeting second = new Greeting();
			second.setMessage("Another one");
			second.setCreatedBy("spring");

			List<GreetingDto> dtos = mapper.toDtos(List.of(first, second));

			assertThat(dtos).hasSize(2);
			assertThat(dtos.get(0).getLabel()).isEqualTo("junit - Hello from a test!");
			assertThat(dtos.get(1).getLabel()).isEqualTo("spring - Another one");
		}

		@Test
		void returnsNullForNullSource() {
			assertThat(mapper.toDtos(null)).isNull();
		}

		@Test
		void returnsEmptyListForEmptyList() {
			assertThat(mapper.toDtos(List.of())).isEmpty();
		}

	}

	@Nested
	@DisplayName("toEntity")
	class ToEntity {

		@Test
		void mapsPayloadToEntity() {
			Greeting greeting = mapper.toEntity(new CreateGreetingRequest("Hello back!", "mapper"));

			assertThat(greeting.getId()).isNull();
			assertThat(greeting.getMessage()).isEqualTo("Hello back!");
			assertThat(greeting.getCreatedBy()).isEqualTo("mapper");
			assertThat(greeting.getCreatedAt()).isNull();
			assertThat(greeting.getUpdatedAt()).isNull();
		}

		@Test
		void returnsNullForNullRequest() {
			assertThat(mapper.toEntity(null)).isNull();
		}

	}

}