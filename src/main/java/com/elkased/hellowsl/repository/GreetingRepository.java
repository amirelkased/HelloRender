package com.elkased.hellowsl.repository;

import com.elkased.hellowsl.entity.Greeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreetingRepository extends JpaRepository<Greeting, Long> {

}