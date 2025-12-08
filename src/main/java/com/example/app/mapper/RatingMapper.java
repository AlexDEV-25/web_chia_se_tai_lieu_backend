package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.RatingResponse;
import com.example.app.model.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	Rating requestToRating(RatingRequest request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "document.id", target = "documentId")
	RatingResponse ratingToResponse(Rating entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "user", ignore = true)
	void updateRating(@MappingTarget Rating entity, RatingRequest request);
}
