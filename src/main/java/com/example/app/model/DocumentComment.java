package com.example.app.model;

import java.util.List;

import com.example.app.model.parent.BaseComment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "document_comments")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DocumentComment extends BaseComment {

	@OneToMany(mappedBy = "parent")
	@JsonManagedReference
	private List<DocumentComment> replies;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	@JsonBackReference
	private DocumentComment parent;

	@ManyToOne
	@JoinColumn(name = "document_id", nullable = false)
	private Document document;

}
