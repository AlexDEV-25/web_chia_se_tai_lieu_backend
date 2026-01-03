package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.RatingDocumentRequest;
import com.example.app.dto.request.RatingLessonRequest;
import com.example.app.dto.response.RatingDocumentResponse;
import com.example.app.dto.response.RatingLessonResponse;
import com.example.app.model.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	Rating ratingDocumentRequestToRating(RatingDocumentRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	Rating ratingLessonRequestToRating(RatingLessonRequest request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "document.id", target = "documentId")
	RatingDocumentResponse ratingToRatingDocumentResponse(Rating entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "lesson.id", target = "lessonId")
	RatingLessonResponse ratingToRatingLessonResponse(Rating entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	void updateRatingDocument(@MappingTarget Rating entity, RatingDocumentRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	void updateRatingLesson(@MappingTarget Rating entity, RatingLessonRequest request);
}
