package com.example.app.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

	public Resource loadImage(String folder, String fileName) throws Exception {
		Path path = Paths.get(folder + "\\" + fileName);

		Resource resource = new UrlResource(path.toUri());

		if (!resource.exists()) {
			throw new RuntimeException("Ảnh không tồn tại: " + fileName);
		}

		return resource;
	}
}
