package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.response.FavoriteDocumentResponse;
import com.example.app.dto.response.FavoriteLessonResponse;
import com.example.app.model.Favorite;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "document.id", target = "documentId")
	@Mapping(source = "document.title", target = "documentTitle")
	@Mapping(source = "document.thumbnailUrl", target = "documentThumbnailUrl")
	@Mapping(source = "document.user.username", target = "authorName")
	FavoriteDocumentResponse favoriteDocumentToResponse(Favorite entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "lesson.id", target = "lessonId")
	@Mapping(source = "lesson.title", target = "lessonTitle")
	@Mapping(source = "lesson.thumbnailUrl", target = "lessonThumbnailUrl")
	@Mapping(source = "lesson.user.username", target = "authorName")
	FavoriteLessonResponse favoriteLessonToResponse(Favorite entity);
}
