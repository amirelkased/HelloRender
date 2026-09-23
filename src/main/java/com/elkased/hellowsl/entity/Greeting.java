package com.elkased.hellowsl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/// A greeting persisted in the in-memory H2 database.
@Entity
@Table(name = "greetings")
@Getter
@Setter
@NoArgsConstructor
public class Greeting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private String createdBy;

	/// Set by Hibernate on insert (UTC).
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	/// Set by Hibernate on every update (UTC).
	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;

}