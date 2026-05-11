package com.example.app.model.parent;

import java.time.LocalDateTime;

import com.example.app.constant.ContentStatus;
import com.example.app.model.Category;
import com.example.app.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
public class BaseContent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Column(name = "views_count")
	private Long viewsCount;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ContentStatus status;

	@Column(name = "hide")
	private boolean hide;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = true)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
