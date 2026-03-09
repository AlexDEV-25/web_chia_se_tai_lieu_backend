package com.example.app.share;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileManager {
	@Value("${libreoffice.path}")
	private String libreOfficePath;

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

	// public void convertFile(String file) {
	// try {
	// FileInputStream docFile = new FileInputStream(new File(file + ".docx"));
	// XWPFDocument doc = new XWPFDocument(docFile);
	// PdfOptions pdfOptions = PdfOptions.create();
	// OutputStream out = new FileOutputStream(new File(file + ".pdf"));
	// PdfConverter.getInstance().convert(doc, out, pdfOptions);
	// doc.close();
	// out.close();
	// System.out.println("Done");
	// } catch (Exception e) {
	// System.out.println("lỗi rồi");
	// }
	// }

}
