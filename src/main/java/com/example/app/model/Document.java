package com.example.app.model;

import java.time.LocalDateTime;
import java.util.List;

import com.example.app.share.DocumentType;
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

@Entity
@Table(name = "documents")
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "file_url", nullable = false)
	private String fileUrl;

	@Column(name = "file_data", nullable = false, columnDefinition = "LONGTEXT")
	@Lob
	private String fileData;

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

	public Document(Long id, String title, String fileUrl, String fileData, DocumentType type, String description,
			String thumbnailUrl, Long viewsCount, Long downloadsCount, LocalDateTime createdAt, boolean hide) {
		this.id = id;
		this.title = title;
		this.fileUrl = fileUrl;
		this.fileData = fileData;
		this.type = type;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
		this.viewsCount = viewsCount;
		this.downloadsCount = downloadsCount;
		this.createdAt = createdAt;
		this.hide = hide;
	}

	public Document() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Long getViewsCount() {
		return viewsCount;
	}

	public void setViewsCount(Long viewsCount) {
		this.viewsCount = viewsCount;
	}

	public Long getDownloadsCount() {
		return downloadsCount;
	}

	public void setDownloadsCount(Long downloadsCount) {
		this.downloadsCount = downloadsCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}
}
