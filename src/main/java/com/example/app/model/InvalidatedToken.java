package com.example.app.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invalidated_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvalidatedToken {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "exprity_time")
	private Date exprityTime;
}
