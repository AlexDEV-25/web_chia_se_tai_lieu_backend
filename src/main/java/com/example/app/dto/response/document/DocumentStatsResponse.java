package com.example.app.dto.response.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatsResponse {
	private long totalDocuments;
	private long totalDownloads;
	private long totalViews;
}
