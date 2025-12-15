package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.FavoriteResponse;
import com.example.app.model.Favorite;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	Favorite requestToFavorite(FavoriteRequest request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "document.id", target = "documentId")
	@Mapping(source = "document.title", target = "documentTitle")
	@Mapping(source = "document.thumbnailUrl", target = "documentThumbnailUrl")
	@Mapping(source = "document.user.username", target = "authorName")
	FavoriteResponse favoriteToResponse(Favorite entity);

}
