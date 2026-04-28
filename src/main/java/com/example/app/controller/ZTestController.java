package com.example.app.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.service.ZTestService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/tests")
@AllArgsConstructor
public class ZTestController {

	private final ZTestService service;

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile request) {
		try {
			Map<?, ?> result = service.uploadFile(request);

			return ResponseEntity.ok(Map.of("url", result.get("secure_url"), "public_id", result.get("public_id")));

		} catch (Exception e) {
			e.printStackTrace(); // ⭐ rất quan trọng
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	// 📄 API xem PDF
	@GetMapping("/pdf")
	public ResponseEntity<byte[]> getPdf(@RequestParam String url) {
		try {
			byte[] data = service.getFile(url);

			return ResponseEntity.ok().header("Content-Type", "application/pdf").body(data);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}

	@GetMapping("/download")
	public ResponseEntity<byte[]> download(@RequestParam String url) {
		try {
			byte[] data = service.downloadFile(url);

			String contentType = "application/octet-stream";
			String fileName = "file";

			if (url.endsWith(".pdf")) {
				contentType = "application/pdf";
				fileName += ".pdf";
			} else if (url.endsWith(".rar")) {
				contentType = "application/vnd.rar";
				fileName += ".rar";
			} else if (url.endsWith(".zip")) {
				contentType = "application/zip";
				fileName += ".zip";
			}

			return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + fileName)
					.header("Content-Type", contentType).body(data);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}

}
