package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.app.constant.ContentStatus;
import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.document.DocumentDetailResponse;
import com.example.app.dto.response.document.DocumentResponse;
import com.example.app.dto.response.document.DocumentStatsResponse;
import com.example.app.dto.response.document.DocumentUserResponse;
import com.example.app.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("DocumentController Tests")
class DocumentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DocumentService documentService;

	@Autowired
	private ObjectMapper objectMapper;

	private DocumentResponse documentResponse;
	private DocumentDetailResponse documentDetailResponse;
	private DocumentStatsResponse documentStatsResponse;
	private DocumentUserResponse documentUserResponse;
	private DocumentRequest documentRequest;

	@BeforeEach
	void setUp() {
		documentResponse = DocumentResponse.builder().id(1L).title("Test Document").description("Test Description")
				.build();

		documentDetailResponse = DocumentDetailResponse.builder().id(1L).title("Test Document")
				.description("Test Description").userId(1L).build();

		documentStatsResponse = DocumentStatsResponse.builder().totalDocuments(100L).totalViews(1000L)
				.totalDownloads(500L).build();

		documentUserResponse = DocumentUserResponse.builder().id(1L).title("Test Document")
				.description("Test Description").build();

		documentRequest = DocumentRequest.builder().title("Test Document").description("Test Description")
				.categoryId(1L).status(ContentStatus.PUBLISHED).hide(false).build();
	}

	@Test
	@DisplayName("GET /api/documents/stats - Should get document statistics")
	void testGetStats_Success() throws Exception {
		when(documentService.getStats()).thenReturn(documentStatsResponse);

		mockMvc.perform(get("/api/documents/stats")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.totalDocuments").value(100L))
				.andExpect(jsonPath("$.result.totalViews").value(1000L));
	}

	@Test
	@DisplayName("GET /api/documents/search - Should search documents")
	void testSearch_Success() throws Exception {
		List<DocumentResponse> searchResults = new ArrayList<>();
		searchResults.add(documentResponse);

		when(documentService.search(anyString(), any())).thenReturn(searchResults);

		mockMvc.perform(get("/api/documents/search").param("keyword", "test").param("categoryId", "1"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.resultList[0].title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents/{id} - Should get document by id")
	void testGetByIdPublicDocument_Success() throws Exception {
		when(documentService.findByIdPublicDocument(anyLong())).thenReturn(documentDetailResponse);

		mockMvc.perform(get("/api/documents/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents - Should get all public documents")
	void testGetAllPublicDocuments_Success() throws Exception {
		List<DocumentResponse> documents = new ArrayList<>();
		documents.add(documentResponse);

		when(documentService.getAllPublicDocuments()).thenReturn(documents);

		mockMvc.perform(get("/api/documents")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents/user - Should get documents by user")
	void testGetByUser_Success() throws Exception {
		List<DocumentResponse> documents = new ArrayList<>();
		documents.add(documentResponse);

		when(documentService.getDocumentsByUser(anyLong(), anyLong())).thenReturn(documents);

		mockMvc.perform(get("/api/documents/user").param("documentId", "1").param("userId", "1"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.resultList[0].title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents/category - Should get documents by category")
	void testGetByCategory_Success() throws Exception {
		List<DocumentResponse> documents = new ArrayList<>();
		documents.add(documentResponse);

		when(documentService.getDocumentsByCategory(anyLong(), anyLong())).thenReturn(documents);

		mockMvc.perform(get("/api/documents/category").param("categoryId", "1").param("documentId", "1"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.resultList[0].title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents/user/{userId} - Should get all documents by user")
	void testGetAllDocumentsByUser_Success() throws Exception {
		List<DocumentResponse> documents = new ArrayList<>();
		documents.add(documentResponse);

		when(documentService.getAllDocumentsByUser(anyLong())).thenReturn(documents);

		mockMvc.perform(get("/api/documents/user/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents/count/{userId} - Should count documents of user")
	void testCountDocumentOfUser_Success() throws Exception {
		when(documentService.countDocumentOfUser(anyLong())).thenReturn(5L);

		mockMvc.perform(get("/api/documents/count/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(5L));
	}

	@Test
	@DisplayName("POST /api/documents/view/{id} - Should increase view count")
	void testIncreaseView_Success() throws Exception {
		doNothing().when(documentService).increaseView(anyLong());

		mockMvc.perform(post("/api/documents/view/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST /api/documents/download/{id} - Should increase download count")
	@WithMockUser(authorities = "INCREASE_DOWNLOAD")
	void testIncreaseDownload_Success() throws Exception {
		doNothing().when(documentService).increaseDownload(anyLong());

		mockMvc.perform(post("/api/documents/download/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /api/documents/{id}/download - Should download document")
	@WithMockUser(authorities = "DOWNLOAD_FILE")
	void testDownload_Success() throws Exception {
		byte[] data = "test".getBytes();
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

		FileResponse file = FileResponse.builder().fileName("abc.pdf").length(data.length)
				.mediaType(MediaType.APPLICATION_PDF).resource(resource).build();

		when(documentService.downloadById(anyLong())).thenReturn(file);

		mockMvc.perform(get("/api/documents/1/download")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST /api/documents/upload-file - Should upload document")
	@WithMockUser(authorities = "UPLOAD_FILE")
	void testCreate_Success() throws Exception {
		when(documentService.uploadFile(any(), any(DocumentRequest.class))).thenReturn(documentDetailResponse);

		String jsonData = objectMapper.writeValueAsString(documentRequest);

		MockMultipartFile data = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());
		MockMultipartFile file = new MockMultipartFile("file", "moimoi.pdf", "application/pdf",
				new byte[] { 1, 2, 3, 4 });

		mockMvc.perform(multipart("/api/documents/upload-file").file(data).file(file)
				.contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents/my-document - Should get current user documents")
	@WithMockUser(authorities = "GET_MY_DOCUMENT")
	void testGetMyDocument_Success() throws Exception {
		List<DocumentUserResponse> documents = new ArrayList<>();
		documents.add(documentUserResponse);

		when(documentService.getMyDocument()).thenReturn(documents);

		mockMvc.perform(get("/api/documents/my-document")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].title").value("Test Document"));
	}

	@Test
	@DisplayName("GET /api/documents/my-document/{id} - Should get current user document detail")
	@WithMockUser(authorities = "GET_MY_DOCUMENT_DETAIL")
	void testGetMyDocumentDetail_Success() throws Exception {
		when(documentService.getMyDocumentDetail(anyLong())).thenReturn(documentDetailResponse);

		mockMvc.perform(get("/api/documents/my-document/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Document"));
	}

	@Test
	@DisplayName("PUT /api/documents/my-document/{id} - Should update document")
	@WithMockUser(authorities = "UPDATE_MY_DOCUMENT")
	void testUpdateMyDocument_Success() throws Exception {
		when(documentService.updateMyDocument(anyLong(), any(DocumentRequest.class))).thenReturn(documentUserResponse);

		mockMvc.perform(put("/api/documents/my-document/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(documentRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Document"));
	}

	@Test
	@DisplayName("DELETE /api/documents/my-document/{id} - Should delete document")
	@WithMockUser(authorities = "DELETE_MY_DOCUMENT")
	void testDeleteMyDocument_Success() throws Exception {
		doNothing().when(documentService).deleteMyDocument(anyLong());

		mockMvc.perform(delete("/api/documents/my-document/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /api/documents/my-document/count - Should count current user documents")
	@WithMockUser(authorities = "COUNT_MY_DOCUMENT")
	void testCountMyDocument_Success() throws Exception {
		when(documentService.countMyDocument()).thenReturn(5L);

		mockMvc.perform(get("/api/documents/my-document/count")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(5L));
	}
}
