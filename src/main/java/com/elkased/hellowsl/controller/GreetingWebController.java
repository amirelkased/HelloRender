package com.elkased.hellowsl.controller;

import com.elkased.hellowsl.dto.CreateGreetingRequest;
import com.elkased.hellowsl.dto.GreetingDto;
import com.elkased.hellowsl.service.GreetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/// Thymeleaf front-end controller.
@Controller
public class GreetingWebController {

	private final GreetingService greetingService;

	public GreetingWebController(GreetingService greetingService) {
		this.greetingService = greetingService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("greetings", greetingService.findAll());
		return "index";
	}

	@PostMapping("/")
	public String create(@RequestParam String message, @RequestParam String createdBy, Model model) {
		GreetingDto saved = greetingService.create(new CreateGreetingRequest(message, createdBy));
		model.addAttribute("saved", saved);
		model.addAttribute("greetings", greetingService.findAll());
		return "index";
	}

}