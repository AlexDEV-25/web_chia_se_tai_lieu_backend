package com.example.app.dto;

public class CategoryDTO {
	private Long id;
	private String name;
	private String description;
	private boolean hide;

	public CategoryDTO(Long id, String name, String description, boolean hide) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.hide = hide;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

}
