package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.response.FavoriteResponse;
import com.example.app.model.Favorite;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "document.id", target = "contentId")
	@Mapping(source = "document.title", target = "title")
	@Mapping(source = "document.thumbnailUrl", target = "thumbnailUrl")
	@Mapping(source = "document.user.username", target = "authorName")
	FavoriteResponse favoriteDocumentToResponse(Favorite entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "lesson.id", target = "contentId")
	@Mapping(source = "lesson.title", target = "title")
	@Mapping(source = "lesson.thumbnailUrl", target = "thumbnailUrl")
	@Mapping(source = "lesson.user.username", target = "authorName")
	FavoriteResponse favoriteLessonToResponse(Favorite entity);
}
