package com.example.app.dto.response;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

public class FileResponse {
	private InputStreamResource resource;
	private long length;
	private MediaType mediaType;

	public FileResponse(InputStreamResource resource, long length, MediaType mediaType) {
		this.resource = resource;
		this.length = length;
		this.mediaType = mediaType;
	}

	public InputStreamResource getResource() {
		return resource;
	}

	public long getLength() {
		return length;
	}

	public MediaType getMediaType() {
		return mediaType;
	}
}
