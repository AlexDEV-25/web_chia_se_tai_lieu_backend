package com.example.app.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.app.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZTestService {
	private final Cloudinary cloudinary;
	private final RestTemplate restTemplate;

	@Value("${app.cloud.name}")
	private String cloudName;

	public Map<?, ?> uploadFile(MultipartFile file) throws Exception {

		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null) {
			originalFilename = "file";
		}

		String lowerName = originalFilename.toLowerCase();

		String publicId;
		String resourceType = "auto";
		String folder = "tailieu";

		// 📦 File nén
		if (lowerName.endsWith(".rar") || lowerName.endsWith(".zip")) {

			resourceType = "raw";
			folder += "/archive"; // ⭐ folder con
			publicId = folder + "/" + UUID.randomUUID() + "_" + originalFilename;

		}
		// 🎬 Video
		else if (lowerName.endsWith(".mp4") || lowerName.endsWith(".avi") || lowerName.endsWith(".mov")) {

			resourceType = "video";
			folder += "/video";
			publicId = folder + "/" + UUID.randomUUID();

		}
		// 🖼 Ảnh
		else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png")
				|| lowerName.endsWith(".webp")) {

			resourceType = "image";
			folder += "/image";
			publicId = folder + "/" + UUID.randomUUID();

		}
		// 📄 PDF
		else if (lowerName.endsWith(".pdf")) {

			resourceType = "image"; // ⭐ để còn tạo thumbnail
			folder += "/pdf";
			publicId = folder + "/" + UUID.randomUUID();

		}
		// 📁 File khác
		else {
			throw new AppException("file sai định dạng", 1001, HttpStatus.BAD_REQUEST);
		}

		return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType,
				"public_id", publicId, "type", "upload", "access_mode", "public"));
	}

	// 📄 Lấy file PDF để hiển thị
	public byte[] getFile(String url) {
		ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

		return response.getBody();
	}

	// 📥 Download file (có thể thêm logic sau này)
	public byte[] downloadFile(String url) {
		ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

		return response.getBody();
	}

	public String getThumbnail(String publicId, String resourceType) {

		if ("video".equals(resourceType)) {
			return "https://res.cloudinary.com/" + cloudName + "/video/upload/so_1/" + publicId + ".jpg";
		} else {
			return "https://res.cloudinary.com/" + cloudName + "/image/upload/pg_1/" + publicId + ".jpg";
		}
	}

}
