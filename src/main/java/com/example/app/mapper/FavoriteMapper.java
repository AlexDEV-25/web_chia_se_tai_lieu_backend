package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.favorite.FavoriteResponse;
import com.example.app.model.Favorite;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

	@Mapping(source = "document.id", target = "contentId")
	@Mapping(source = "document.title", target = "title")
	@Mapping(source = "document.thumbnailUrl", target = "thumbnailUrl")
	@Mapping(source = "document.user.username", target = "authorName")
	FavoriteResponse favoriteDocumentToResponse(Favorite entity);

	@Mapping(source = "lesson.id", target = "contentId")
	@Mapping(source = "lesson.title", target = "title")
	@Mapping(source = "lesson.thumbnailUrl", target = "thumbnailUrl")
	@Mapping(source = "lesson.user.username", target = "authorName")
	FavoriteResponse favoriteLessonToResponse(Favorite entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	Favorite requestToFavorite(FavoriteRequest request);

}
