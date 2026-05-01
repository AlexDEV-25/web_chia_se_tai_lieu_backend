package com.example.app.share;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.app.dto.response.FileResponse;
import com.example.app.exception.AppException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileManager {

	private final Cloudinary cloudinary;
	private final RestTemplate restTemplate;

	@Value("${app.cloud.name}")
	private String cloudName;

	public FileResponse downloadFile(String url) throws Exception {

		ResponseEntity<Resource> response = restTemplate.getForEntity(url, Resource.class);

		Resource resource = response.getBody();

		if (resource == null) {
			throw new RuntimeException("Không lấy được file");
		}

		// Lấy InputStream
		InputStreamResource inputStreamResource = new InputStreamResource(resource.getInputStream());

		// Lấy content-type
		MediaType mediaType = response.getHeaders().getContentType();
		if (mediaType == null) {
			mediaType = MediaType.APPLICATION_OCTET_STREAM;
		}

		// Lấy content-length
		long contentLength = response.getHeaders().getContentLength();

		// Lấy tên file từ URL
		String fileName = url.substring(url.lastIndexOf("/") + 1);

		return new FileResponse(inputStreamResource, contentLength, mediaType, fileName);
	}

	public String getThumbnail(String publicId, String resourceType) {

		if ("video".equals(resourceType)) {
			return "https://res.cloudinary.com/" + cloudName + "/video/upload/so_1/" + publicId + ".jpg";
		} else {
			return "https://res.cloudinary.com/" + cloudName + "/image/upload/pg_1/" + publicId + ".jpg";
		}
	}

	public Map<?, ?> deleteFile(String url, String resourceType) throws Exception {
		String publicId = extractPublicId(url);

		return cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType // ⭐ rất quan
																										// trọng
		));
	}

	public String fileName(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null) {
			originalFilename = "file";
		}

		return originalFilename.toLowerCase();
	}

	public Map<?, ?> uploadImage(MultipartFile file) throws Exception {
		String avatarName = this.fileName(file);
		if (avatarName.endsWith(".jpg") || avatarName.endsWith(".jpeg") || avatarName.endsWith(".png")
				|| avatarName.endsWith(".webp")) {
			String folder = "tailieu/image";
			String publicId = folder + "/" + UUID.randomUUID();

			return uploadToCloudinary(file, "image", publicId);
		} else {
			throw new AppException("ảnh  không đúng định dạng", 1001, HttpStatus.BAD_REQUEST);
		}

	}

	public Map<?, ?> uploadVideo(MultipartFile file) throws Exception {
		String videoName = this.fileName(file);
		if (videoName.endsWith(".mp4") || videoName.endsWith(".avi") || videoName.endsWith(".mov")) {
			String folder = "tailieu/video";
			String publicId = folder + "/" + UUID.randomUUID();

			return uploadToCloudinary(file, "video", publicId);
		} else {
			throw new AppException("video không đúng định dạng", 1001, HttpStatus.BAD_REQUEST);
		}

	}

	public Map<?, ?> uploadArchive(MultipartFile file, String originalFilename) throws Exception {
		String subFileName = this.fileName(file);
		if (subFileName.endsWith(".rar") || subFileName.endsWith(".zip")) {
			String folder = "tailieu/archive";
			String publicId = folder + "/" + UUID.randomUUID() + "_" + originalFilename;

			return uploadToCloudinary(file, "raw", publicId);
		} else {
			throw new AppException("sub file không đúng định dạng", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public Map<?, ?> uploadPdf(MultipartFile file) throws Exception {
		String documentName = this.fileName(file);
		if (documentName.endsWith(".pdf")) {
			String folder = "tailieu/pdf";
			String publicId = folder + "/" + UUID.randomUUID();
			return uploadToCloudinary(file, "image", publicId);
		} else {
			throw new AppException("document không đúng định dạng", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	private Map<?, ?> uploadToCloudinary(MultipartFile file, String resourceType, String publicId) throws Exception {

		return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType,
				"public_id", publicId, "type", "upload", "access_mode", "public"));
	}

	private String extractPublicId(String url) {

		// Lấy phần sau "/upload/"
		int index = url.indexOf("/upload/");
		String path = url.substring(index + 8);

		// Bỏ version (v123456/)
		path = path.replaceFirst("v\\d+/", "");

		// Bỏ extension (.pdf, .jpg, .zip...)
		int dotIndex = path.lastIndexOf(".");
		if (dotIndex != -1) {
			path = path.substring(0, dotIndex);
		}

		return path;
	}

}
