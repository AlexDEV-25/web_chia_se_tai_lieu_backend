package com.example.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

	private final ImageService imageService;

	@Value("${app.storage-directory-image}")
	private String thumbnailStorage;

	@Value("${app.storage-directory-avatar}")
	private String avatarStorage;

	@GetMapping("/avatar/{name}")
	public ResponseEntity<Resource> getAvatar(@PathVariable String name) {
		try {
			Resource resource = imageService.loadImage(avatarStorage, name);

			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);

		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/thumbnail/{name}")
	public ResponseEntity<Resource> getThumbnail(@PathVariable String name) {
		try {
			Resource resource = imageService.loadImage(thumbnailStorage, name);

			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);

		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
}
