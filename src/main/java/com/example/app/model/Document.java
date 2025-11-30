package com.example.app.model;

import java.time.LocalDateTime;
import java.util.List;

import com.example.app.share.DocumentType;
import com.example.app.share.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "file_url", nullable = false)
	private String fileUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private DocumentType type = DocumentType.PDF;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Column(name = "views_count")
	private Long viewsCount = 0L;

	@Column(name = "downloads_count")
	private Long downloadsCount = 0L;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status = Status.PENDING;

	@Column(name = "hide")
	private boolean hide;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JsonBackReference
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JsonBackReference
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JsonManagedReference
	private List<Comment> comments;

	@OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JsonManagedReference
	private List<Rating> ratings;

	@OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JsonManagedReference
	private List<Favorite> favorites;
}
