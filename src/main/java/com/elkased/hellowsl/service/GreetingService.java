package com.elkased.hellowsl.service;

import com.elkased.hellowsl.dto.CreateGreetingRequest;
import com.elkased.hellowsl.dto.GreetingDto;
import com.elkased.hellowsl.entity.Greeting;
import com.elkased.hellowsl.mapper.GreetingMapper;
import com.elkased.hellowsl.repository.GreetingRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GreetingService {

	private final GreetingRepository greetingRepository;

	private final GreetingMapper greetingMapper;

	public GreetingService(GreetingRepository greetingRepository, GreetingMapper greetingMapper) {
		this.greetingRepository = greetingRepository;
		this.greetingMapper = greetingMapper;
	}

	@Transactional(readOnly = true)
	public List<GreetingDto> findAll() {
		return greetingMapper.toDtos(greetingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
	}

	@Transactional(readOnly = true)
	public Optional<GreetingDto> findById(Long id) {
		return greetingRepository.findById(id).map(greetingMapper::toDto);
	}

	@Transactional
	public GreetingDto create(CreateGreetingRequest request) {
		return greetingMapper.toDto(greetingRepository.save(greetingMapper.toEntity(request)));
	}

	@Transactional
	public boolean deleteById(Long id) {
		if (!greetingRepository.existsById(id)) {
			return false;
		}
		greetingRepository.deleteById(id);
		return true;
	}

}