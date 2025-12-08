package com.example.app.share;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

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
			ProcessBuilder pb = new ProcessBuilder(libreOfficePath, "--headless", "--convert-to", "pdf", "--outdir",
					new File(output).getParent(), input);

			Process process = pb.start();
			process.waitFor();
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public void convertFile(String file) {
//	try {
//		FileInputStream docFile = new FileInputStream(new File(file + ".docx"));
//		XWPFDocument doc = new XWPFDocument(docFile);
//		PdfOptions pdfOptions = PdfOptions.create();
//		OutputStream out = new FileOutputStream(new File(file + ".pdf"));
//		PdfConverter.getInstance().convert(doc, out, pdfOptions);
//		doc.close();
//		out.close();
//		System.out.println("Done");
//	} catch (Exception e) {
//		System.out.println("lỗi rồi");
//	}
//}

}
