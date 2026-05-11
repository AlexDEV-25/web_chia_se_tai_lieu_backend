package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.response.category.CategoryResponse;
import com.example.app.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

	CategoryResponse entityToResponse(Category entity);

	@Mapping(target = "id", ignore = true)
	void updateCategory(@MappingTarget Category entity, CategoryRequest request);

}
