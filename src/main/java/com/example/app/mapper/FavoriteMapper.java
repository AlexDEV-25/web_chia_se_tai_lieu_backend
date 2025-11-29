package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.FavoriteResponse;
import com.example.app.model.Favorite;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	Favorite requestToFavorite(FavoriteRequest request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "document.id", target = "documentId")
	@Mapping(source = "document.title", target = "documentTitle")
	@Mapping(source = "document.thumbnailUrl", target = "documentThumbnailUrl")
	FavoriteResponse favoriteToResponse(Favorite entity);

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	Favorite responseToFavorite(FavoriteResponse Response);

}
