package com.example.app.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	public static final String STORAGE_DIRECTORY = "G:\\web_DATN\\storage";

	public String saveFile(MultipartFile fileToSave) throws IOException {
		if (fileToSave == null) {
			throw new NullPointerException("fileToSave is null");
		}
		String originalFilename = fileToSave.getOriginalFilename();
		String safeFilename = UUID.randomUUID() + "_"
				+ (originalFilename != null ? Paths.get(originalFilename).getFileName().toString() : "file");

		var targetFile = new File(STORAGE_DIRECTORY + File.separator + fileToSave.getOriginalFilename());
		if (!Objects.equals(targetFile.getParent(), STORAGE_DIRECTORY)) {
			throw new SecurityException("Unsupported filename!");
		}
		Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return safeFilename;
	}

	public File getDownloadFile(String fileName) throws Exception {
		if (fileName == null) {
			throw new NullPointerException("fileName is null");
		}
		var fileToDownload = new File(STORAGE_DIRECTORY + File.separator + fileName);
		if (!Objects.equals(fileToDownload.getParent(), STORAGE_DIRECTORY)) {
			throw new SecurityException("Unsupported filename!");
		}
		if (!fileToDownload.exists()) {
			throw new FileNotFoundException("No file named: " + fileName);
		}
		return fileToDownload;
	}
}