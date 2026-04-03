package com.example.app.share;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.exception.AppException;

@Component
public class FileManager {
	@Value("${libreoffice.path}")
	private String libreOfficePath;

	@Value("${app.storage-directory-document}")
	private String documentStorage;

	@Value("${app.storage-directory-image}")
	private String thumbnailStorage;

	@Value("${app.storage-directory-video}")
	private String videoStorage;

	@Value("${app.storage-directory-subfile}")
	private String subfileStorage;

	@Value("${app.storage-directory-avatar}")
	private String avatarStorage;

	public String saveFile(MultipartFile fileToSave, String storageDirectory) throws IOException {
		if (fileToSave == null) {
			throw new NullPointerException("fileToSave is null");
		}

		// Lấy tên file thật sự, tránh ../../ hack
		String originalFilename = fileToSave.getOriginalFilename();
		String cleanedFilename = originalFilename != null ? Paths.get(originalFilename).getFileName().toString()
				: "file";

		// Tạo tên an toàn
		String safeFilename = UUID.randomUUID() + "_" + cleanedFilename;

		File targetFile = new File(storageDirectory, safeFilename);

		// Kiểm tra path traversal (bắt buộc)
		if (!targetFile.getCanonicalPath().startsWith(new File(storageDirectory).getCanonicalPath())) {
			throw new SecurityException("Invalid file path!");
		}

		// Copy file
		Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return safeFilename;
	}

	public boolean deleteFile(String filePath) {
		try {
			Path path = Paths.get(filePath);
			return Files.deleteIfExists(path);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void convertToPDF(String input, String output) {
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

			if (!generated.getCanonicalPath().equals(outputFile.getCanonicalPath())) {
				Files.move(generated.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			throw new RuntimeException("convertToPDF failed: " + e.getMessage(), e);
		}
	}

	public ResponseEntity<Resource> getVideo(File video, @RequestHeader HttpHeaders headers) throws IOException {

		long fileLength = video.length();

		String range = headers.getFirst(HttpHeaders.RANGE);

		// 👉 Trường hợp browser chưa seek
		if (range == null) {
			return ResponseEntity.ok().contentType(MediaType.valueOf("video/mp4")).contentLength(fileLength)
					.header(HttpHeaders.ACCEPT_RANGES, "bytes").body(new FileSystemResource(video));
		}

		// 👉 Browser yêu cầu seek
		long start = Long.parseLong(range.replace("bytes=", "").replace("-", ""));
		long end = fileLength - 1;
		long contentLength = end - start + 1;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(HttpHeaders.CONTENT_TYPE, "video/mp4");
		responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
		responseHeaders.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);
		responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

		InputStream inputStream = new FileInputStream(video);
		inputStream.skip(start);

		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(responseHeaders)
				.body(new InputStreamResource(inputStream));
	}

	public String handleFile(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();

		if (!isValidFile(fileName)) {
			throw new AppException("file không hợp lệ", 1001, HttpStatus.BAD_REQUEST);
		}

		String fileUrl = saveFile(file, documentStorage);

		if (!fileUrl.endsWith(".pdf")) {
			int index = fileUrl.lastIndexOf(".");
			String result = (index != -1) ? fileUrl.substring(0, index) + ".pdf" : fileUrl;

			String input = documentStorage + File.separator + fileUrl;
			String output = documentStorage + File.separator + result;

			convertToPDF(input, output);

			fileUrl = result;
		}

		return fileUrl;
	}

	private boolean isValidFile(String fileName) {
		return fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx")
				|| fileName.endsWith(".ppt") || fileName.endsWith(".pptx");
	}

	public String handleDocumentThumbnail(String url) {

		File docFile = new File(url);
		// Load pdf
		try (PDDocument doc = PDDocument.load(docFile)) {
			// Render trang đầu
			PDFRenderer renderer = new PDFRenderer(doc);
			BufferedImage image = renderer.renderImageWithDPI(0, 150); // 0 = trang đầu

			// Lưu ra ảnh PNG
			String thumbnailUrl = UUID.randomUUID().toString() + ".png";
			String outputPath = thumbnailStorage + File.separator + thumbnailUrl;
			ImageIO.write(image, "png", new File(outputPath));
			return thumbnailUrl;
		} catch (Exception e) {
			throw new AppException(e.getMessage(), 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public String handleVideo(MultipartFile video) throws IOException {
		String fileName = video.getOriginalFilename();
		if (fileName.endsWith(".mp4")) {
			String fileUrl = saveFile(video, videoStorage);
			return fileUrl;
		} else {
			throw new AppException("video không hợp lệ", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public String handleSubFile(MultipartFile subFile) throws IOException {
		String fileName = subFile.getOriginalFilename();
		if (fileName.endsWith(".rar")) {
			String fileUrl = saveFile(subFile, subfileStorage);
			return fileUrl;
		} else {
			throw new AppException("file không hợp lệ", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public String handleVideoThumbnail(String input) {
		FFmpegFrameGrabber grabber = null;

		String thumbnailUrl = UUID.randomUUID().toString() + ".jpg";
		String output = thumbnailStorage + File.separator + thumbnailUrl;

		try {
			grabber = new FFmpegFrameGrabber(input);
			grabber.start();

			Frame frame;
			BufferedImage image = null;

			// Bỏ qua frame audio, chỉ lấy frame hình
			while ((frame = grabber.grab()) != null) {
				if (frame.image != null) {
					try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
						image = converter.convert(frame);
					}
					break;
				}
			}

			if (image == null) {
				throw new RuntimeException("Không lấy được frame hình từ video");
			}

			ImageIO.write(image, "jpg", new File(output));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (grabber != null)
					grabber.stop();
			} catch (Exception ignored) {
			}
		}

		return thumbnailUrl;
	}

	public String handleAvatar(String oldAvatarUrl, MultipartFile newAvatar) throws IOException {
		if (oldAvatarUrl != null) {
			deleteFile(avatarStorage + File.separator + oldAvatarUrl);
		}

		if (newAvatar.getOriginalFilename().endsWith(".png") || newAvatar.getOriginalFilename().endsWith(".jpg")) {
			String avtUrl = saveFile(newAvatar, avatarStorage);
			return avtUrl;
		} else {
			throw new AppException("ảnh không đúng định dạng", 1001, HttpStatus.BAD_REQUEST);
		}
	}
}
