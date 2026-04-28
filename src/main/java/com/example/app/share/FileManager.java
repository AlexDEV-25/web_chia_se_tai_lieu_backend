package com.example.app.share;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
	@Value("${libreoffice.path}")
	private String libreOfficePath;

	@Value("${app.storage-directory-document}")
	private String documentStorage;

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

	public Map<?, ?> handleDocument(MultipartFile file) throws Exception {
		String fileName = file.getOriginalFilename();

		if (!isValidFile(fileName)) {
			throw new AppException("file không hợp lệ", 1001, HttpStatus.BAD_REQUEST);
		}

		if (!fileName.endsWith(".pdf")) {
			String fileUrl = saveFileToLocalStorage(file);

			int index = fileUrl.lastIndexOf(".");
			String result = (index != -1) ? fileUrl.substring(0, index) + ".pdf" : fileUrl;

			String input = documentStorage + File.separator + fileUrl;
			String output = documentStorage + File.separator + result;

			File fileAfterConvert = convertToPDF(input, output);

			deleteFileLocal(input);

			return uploadLocalFile(fileAfterConvert);

		}
		return uploadPdf(file);
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

		String folder = "tailieu/image";
		String publicId = folder + "/" + UUID.randomUUID();

		return uploadToCloudinary(file, "image", publicId);
	}

	public Map<?, ?> uploadVideo(MultipartFile file) throws Exception {

		String folder = "tailieu/video";
		String publicId = folder + "/" + UUID.randomUUID();

		return uploadToCloudinary(file, "video", publicId);
	}

	public Map<?, ?> uploadArchive(MultipartFile file, String originalFilename) throws Exception {

		String folder = "tailieu/archive";
		String publicId = folder + "/" + UUID.randomUUID() + "_" + originalFilename;

		return uploadToCloudinary(file, "raw", publicId);
	}

	private String saveFileToLocalStorage(MultipartFile fileToSave) throws IOException {
		if (fileToSave == null) {
			throw new NullPointerException("fileToSave is null");
		}

		// Lấy tên file thật sự, tránh ../../ hack
		String originalFilename = fileToSave.getOriginalFilename();
		String cleanedFilename = originalFilename != null ? Paths.get(originalFilename).getFileName().toString()
				: "file";

		// Tạo tên an toàn
		String safeFilename = UUID.randomUUID() + "_" + cleanedFilename;

		File targetFile = new File(documentStorage, safeFilename);

		// Kiểm tra path traversal (bắt buộc)
		if (!targetFile.getCanonicalPath().startsWith(new File(documentStorage).getCanonicalPath())) {
			throw new SecurityException("Invalid file path!");
		}

		// Copy file
		Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return safeFilename;
	}

	private File convertToPDF(String input, String output) {
		try {
			if (libreOfficePath == null || libreOfficePath.isBlank()) {
				throw new IllegalStateException("libreoffice.path is missing");
			}

			File officeExe = new File(libreOfficePath);
			if (!officeExe.exists()) {
				throw new IllegalStateException("LibreOffice executable not found: " + libreOfficePath);
			}

			File inputFile = new File(input);
			if (!inputFile.exists()) {
				throw new IllegalArgumentException("Input file not found: " + input);
			}

			File outputFile = new File(output);
			File outDir = outputFile.getParentFile();

			if (outDir == null) {
				throw new IllegalArgumentException("Invalid output path: " + output);
			}

			Files.createDirectories(outDir.toPath());

			// 🔥 Run LibreOffice
			ProcessBuilder pb = new ProcessBuilder(libreOfficePath, "--headless", "--nologo", "--nolockcheck",
					"--norestore", "--convert-to", "pdf", "--outdir", outDir.getAbsolutePath(),
					inputFile.getAbsolutePath());

			pb.redirectErrorStream(true);
			Process process = pb.start();

			boolean finished = process.waitFor(120, TimeUnit.SECONDS);
			String outputLog = new String(process.getInputStream().readAllBytes());

			if (!finished) {
				process.destroyForcibly();
				throw new IllegalStateException("LibreOffice conversion timed out. Log: " + outputLog);
			}

			int exitCode = process.exitValue();
			if (exitCode != 0) {
				throw new IllegalStateException(
						"LibreOffice conversion failed with exitCode=" + exitCode + ". Log: " + outputLog);
			}

			// 🔹 File PDF được tạo ra
			String baseName = inputFile.getName();
			int dot = baseName.lastIndexOf('.');
			if (dot != -1) {
				baseName = baseName.substring(0, dot);
			}

			File generated = new File(outDir, baseName + ".pdf");

			if (!generated.exists()) {
				throw new IllegalStateException(
						"Converted PDF not found at: " + generated.getAbsolutePath() + ". Log: " + outputLog);
			}

			// 🔹 Nếu khác tên output thì move
			if (!generated.getCanonicalPath().equals(outputFile.getCanonicalPath())) {
				Files.move(generated.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			// 🔥 RETURN FILE
			return outputFile;

		} catch (Exception e) {
			throw new RuntimeException("convertToPDF failed: " + e.getMessage(), e);
		}
	}

	private boolean deleteFileLocal(String filePath) {
		try {
			Path path = Paths.get(filePath);
			return Files.deleteIfExists(path);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isValidFile(String fileName) {
		return fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx")
				|| fileName.endsWith(".ppt") || fileName.endsWith(".pptx");
	}

	private Map<?, ?> uploadToCloudinary(MultipartFile file, String resourceType, String publicId) throws Exception {

		return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType,
				"public_id", publicId, "type", "upload", "access_mode", "public"));
	}

	private Map<?, ?> uploadPdf(MultipartFile file) throws Exception {

		String folder = "tailieu/pdf";
		String publicId = folder + "/" + UUID.randomUUID();

		// dùng image để có thumbnail
		return uploadToCloudinary(file, "image", publicId);
	}

	private Map<?, ?> uploadLocalFile(File file) throws Exception {

		String originalFilename = file.getName();
		String lowerName = originalFilename.toLowerCase();

		String publicId;
		String resourceType = "auto";
		String folder = "tailieu";

		if (lowerName.endsWith(".pdf")) {
			resourceType = "image";
			folder += "/pdf";
			publicId = folder + "/" + UUID.randomUUID();

		} else {
			throw new RuntimeException("File không hợp lệ");
		}

		return cloudinary.uploader().upload(file,
				ObjectUtils.asMap("resource_type", resourceType, "public_id", publicId));
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
