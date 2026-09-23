package com.elkased.hellowsl.service;

import com.elkased.hellowsl.dto.CreateGreetingRequest;
import com.elkased.hellowsl.dto.GreetingDto;
import com.elkased.hellowsl.entity.Greeting;
import com.elkased.hellowsl.mapper.GreetingMapper;
import com.elkased.hellowsl.repository.GreetingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// Unit tests for GreetingService covering happy path, edge and error scenarios.
@ExtendWith(MockitoExtension.class)
@DisplayName("GreetingService")
class GreetingServiceTest {

	@Mock
	private GreetingRepository greetingRepository;

	@Mock
	private GreetingMapper greetingMapper;

	@InjectMocks
	private GreetingService greetingService;

	private Greeting sampleEntity() {
		Greeting greeting = new Greeting();
		greeting.setId(1L);
		greeting.setMessage("Hello!");
		greeting.setCreatedBy("junit");
		greeting.setCreatedAt(Instant.now());
		return greeting;
	}

	private GreetingDto sampleDto(Greeting greeting) {
		return GreetingDto.builder()
			.id(greeting.getId())
			.message(greeting.getMessage())
			.createdBy(greeting.getCreatedBy())
			.createdAt(greeting.getCreatedAt())
			.label(greeting.getCreatedBy() + " - " + greeting.getMessage())
			.build();
	}

	@Nested
	@DisplayName("findAll")
	class FindAll {

		@Test
		void returnsMappedGreetingsNewestFirst() {
			Greeting greeting = sampleEntity();
			when(greetingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))).thenReturn(List.of(greeting));
			when(greetingMapper.toDtos(List.of(greeting))).thenReturn(List.of(sampleDto(greeting)));

			assertThat(greetingService.findAll()).extracting(GreetingDto::getMessage).containsExactly("Hello!");
		}

		@Test
		void returnsEmptyListWhenNoGreetings() {
			when(greetingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))).thenReturn(List.of());
			when(greetingMapper.toDtos(List.of())).thenReturn(List.of());

			assertThat(greetingService.findAll()).isEmpty();
		}

	}

	@Nested
	@DisplayName("findById")
	class FindById {

		@Test
		void returnsDtoWhenFound() {
			Greeting greeting = sampleEntity();
			when(greetingRepository.findById(1L)).thenReturn(Optional.of(greeting));
			when(greetingMapper.toDto(greeting)).thenReturn(sampleDto(greeting));

			assertThat(greetingService.findById(1L)).contains(sampleDto(greeting));
		}

		@Test
		void returnsEmptyWhenMissing() {
			when(greetingRepository.findById(99L)).thenReturn(Optional.empty());

			assertThat(greetingService.findById(99L)).isEmpty();
		}

	}

	@Nested
	@DisplayName("create")
	class Create {

		@Test
		void persistsMappedEntityAndReturnsDto() {
			CreateGreetingRequest request = new CreateGreetingRequest("Hello!", "junit");
			Greeting toSave = sampleEntity();
			Greeting saved = sampleEntity();
			GreetingDto dto = sampleDto(saved);

			when(greetingMapper.toEntity(request)).thenReturn(toSave);
			when(greetingRepository.save(toSave)).thenReturn(saved);
			when(greetingMapper.toDto(saved)).thenReturn(dto);

			assertThat(greetingService.create(request)).isEqualTo(dto);
			verify(greetingRepository).save(toSave);
		}

	}

	@Nested
	@DisplayName("deleteById")
	class DeleteById {

		@Test
		void deletesAndReturnsTrueWhenExists() {
			when(greetingRepository.existsById(1L)).thenReturn(true);

			assertThat(greetingService.deleteById(1L)).isTrue();
			verify(greetingRepository).deleteById(1L);
		}

		@Test
		void returnsFalseWithoutDeletingWhenMissing() {
			when(greetingRepository.existsById(99L)).thenReturn(false);

			assertThat(greetingService.deleteById(99L)).isFalse();
			verify(greetingRepository, never()).deleteById(any());
		}

	}

}