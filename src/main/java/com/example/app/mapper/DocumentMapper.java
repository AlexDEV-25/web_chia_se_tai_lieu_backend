package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.DocumentResponse;
import com.example.app.model.Document;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	Document requestToDocument(DocumentRequest Request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "category.id", target = "categoryId")
	@Mapping(source = "category.name", target = "categoryName")
	DocumentResponse documentToResponse(Document entity);

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	Document responseToDocument(DocumentResponse Response);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	void updateDocument(@MappingTarget Document document, DocumentRequest Request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "title", ignore = true)
	@Mapping(target = "fileUrl", ignore = true)
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "description", ignore = true)
	@Mapping(target = "thumbnailUrl", ignore = true)
	@Mapping(target = "viewsCount", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateDownloadCount(@MappingTarget Document document, DocumentRequest Request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "title", ignore = true)
	@Mapping(target = "fileUrl", ignore = true)
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "description", ignore = true)
	@Mapping(target = "thumbnailUrl", ignore = true)
	@Mapping(target = "viewsCount", ignore = true)
	@Mapping(target = "downloadsCount", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateStatus(@MappingTarget Document document, DocumentRequest Request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "title", ignore = true)
	@Mapping(target = "fileUrl", ignore = true)
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "description", ignore = true)
	@Mapping(target = "thumbnailUrl", ignore = true)
	@Mapping(target = "viewsCount", ignore = true)
	@Mapping(target = "downloadsCount", ignore = true)
	@Mapping(target = "status", ignore = true)
	void hideDocument(@MappingTarget Document document, HideRequest Request);

}
