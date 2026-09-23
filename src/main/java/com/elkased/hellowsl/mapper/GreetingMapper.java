package com.elkased.hellowsl.mapper;

import com.elkased.hellowsl.dto.CreateGreetingRequest;
import com.elkased.hellowsl.dto.GreetingDto;
import com.elkased.hellowsl.entity.Greeting;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/// Maps between the Greeting entity and its DTOs.
///
/// Builder is disabled on purpose: with Lombok @Builder targets, MapStruct would
/// never call the @AfterMapping lifecycle method with the real target type.
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface GreetingMapper {

	@Mapping(target = "label", ignore = true)
	GreetingDto toDto(Greeting greeting);

	List<GreetingDto> toDtos(List<Greeting> greetings);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Greeting toEntity(CreateGreetingRequest request);

	/// Derives the display label after the plain field mapping completes.
	@AfterMapping
	default void fillLabel(Greeting greeting, @MappingTarget GreetingDto dto) {
		dto.setLabel("%s - %s".formatted(greeting.getCreatedBy(), greeting.getMessage()));
	}

}