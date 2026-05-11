package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.rating.RatingUserResponse;
import com.example.app.model.DocumentRating;
import com.example.app.model.LessonRating;

@Mapper(componentModel = "spring")
public interface RatingMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	DocumentRating requestToDocumentRating(RatingRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	LessonRating requestToLessonRating(RatingRequest request);

	RatingUserResponse documentRatingToRatingResponse(DocumentRating entity);

	RatingUserResponse lessonRatingToRatingResponse(LessonRating entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "user", ignore = true)
	void updateDocumentRating(@MappingTarget DocumentRating entity, RatingRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	void updateLessonRating(@MappingTarget LessonRating entity, RatingRequest request);
}
