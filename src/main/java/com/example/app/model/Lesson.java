package com.example.app.model;

import com.example.app.model.parent.BaseContent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "lessons")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Lesson extends BaseContent {

	@Column(name = "lesson_url", nullable = false)
	private String lessonUrl;

	@Column(name = "document_url")
	private String documentUrl;

	@Column(name = "sub_file_url")
	private String subFileUrl;

}
