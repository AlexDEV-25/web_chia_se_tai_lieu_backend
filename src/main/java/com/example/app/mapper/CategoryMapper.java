package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.CategoryResponse;
import com.example.app.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "documents", ignore = true)
	Category requestToCategory(CategoryRequest Request);

	CategoryResponse categoryToResponse(Category entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateCategory(@MappingTarget Category entity, CategoryRequest Request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "description", ignore = true)
	void hideCategory(@MappingTarget Category entity, HideRequest Request);
}
