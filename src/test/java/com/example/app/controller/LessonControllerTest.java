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
import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.lesson.LessonDetailResponse;
import com.example.app.dto.response.lesson.LessonResponse;
import com.example.app.dto.response.lesson.LessonStatsResponse;
import com.example.app.dto.response.lesson.LessonUserResponse;
import com.example.app.service.LessonService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("LessonController Tests")
class LessonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LessonService lessonService;

	@Autowired
	private ObjectMapper objectMapper;

	private LessonResponse lessonResponse;
	private LessonDetailResponse lessonDetailResponse;
	private LessonStatsResponse lessonStatsResponse;
	private LessonUserResponse lessonUserResponse;
	private LessonRequest lessonRequest;

	@BeforeEach
	void setUp() {
		lessonResponse = LessonResponse.builder().id(1L).title("Test Lesson").description("Test Description").build();

		lessonDetailResponse = LessonDetailResponse.builder().id(1L).title("Test Lesson")
				.description("Test Description").userId(1L).build();

		lessonStatsResponse = LessonStatsResponse.builder().totalLessons(50L).totalViews(500L).build();

		lessonUserResponse = LessonUserResponse.builder().id(1L).title("Test Lesson").description("Test Description")
				.build();

		lessonRequest = LessonRequest.builder().title("Test Lesson").description("Test Description").categoryId(1L)
				.status(ContentStatus.PUBLISHED).hide(false).build();
	}

	@Test
	@DisplayName("GET /api/lessons/stats - Should get lesson statistics")
	void testGetStats_Success() throws Exception {
		when(lessonService.getStats()).thenReturn(lessonStatsResponse);

		mockMvc.perform(get("/api/lessons/stats")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.totalLessons").value(50L))
				.andExpect(jsonPath("$.result.totalViews").value(500L));
	}

	@Test
	@DisplayName("GET /api/lessons/search - Should search lessons")
	void testSearch_Success() throws Exception {
		List<LessonResponse> searchResults = new ArrayList<>();
		searchResults.add(lessonResponse);

		when(lessonService.search(anyString(), any())).thenReturn(searchResults);

		mockMvc.perform(get("/api/lessons/search").param("keyword", "test").param("categoryId", "1"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.resultList[0].title").value("Test Lesson"));
	}

	@Test
	@DisplayName("GET /api/lessons/{id} - Should get lesson by id")
	void testGetByIdPublicLesson_Success() throws Exception {
		when(lessonService.findByIdPublicLesson(anyLong())).thenReturn(lessonDetailResponse);

		mockMvc.perform(get("/api/lessons/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Lesson"));
	}

	@Test
	@DisplayName("GET /api/lessons - Should get all public lessons")
	void testGetAllPublicLesson_Success() throws Exception {
		List<LessonResponse> lessons = new ArrayList<>();
		lessons.add(lessonResponse);

		when(lessonService.getAllPublicLessonsCheckFavorite()).thenReturn(lessons);

		mockMvc.perform(get("/api/lessons")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].title").value("Test Lesson"));
	}

	@Test
	@DisplayName("GET /api/lessons/user - Should get lessons by user")
	void testGetByUser_Success() throws Exception {
		List<LessonResponse> lessons = new ArrayList<>();
		lessons.add(lessonResponse);

		when(lessonService.getLessonsByUserCheckFavorite(anyLong(), anyLong())).thenReturn(lessons);

		mockMvc.perform(get("/api/lessons/user").param("lessonId", "1").param("userId", "1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].title").value("Test Lesson"));
	}

	@Test
	@DisplayName("GET /api/lessons/user/{userId} - Should get all lessons by user")
	void testGetAllLessonsByUser_Success() throws Exception {
		List<LessonResponse> lessons = new ArrayList<>();
		lessons.add(lessonResponse);

		when(lessonService.getAllLessonsByUserCheckFavorite(anyLong())).thenReturn(lessons);

		mockMvc.perform(get("/api/lessons/user/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].title").value("Test Lesson"));
	}

	@Test
	@DisplayName("GET /api/lessons/category - Should get lessons by category")
	void testGetByCategory_Success() throws Exception {
		List<LessonResponse> lessons = new ArrayList<>();
		lessons.add(lessonResponse);

		when(lessonService.getLessonsByCategoryCheckFavorite(anyLong(), anyLong())).thenReturn(lessons);

		mockMvc.perform(get("/api/lessons/category").param("categoryId", "1").param("lessonId", "1"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.resultList[0].title").value("Test Lesson"));
	}

	@Test
	@DisplayName("POST /api/lessons/view/{id} - Should increase view count")
	void testIncreaseView_Success() throws Exception {
		doNothing().when(lessonService).increaseView(anyLong());

		mockMvc.perform(post("/api/lessons/view/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /api/lessons/count/{userId} - Should count lessons of user")
	void testCountLessonOfUser_Success() throws Exception {
		when(lessonService.countLessonOfUser(anyLong())).thenReturn(3L);

		mockMvc.perform(get("/api/lessons/count/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(3L));
	}

	@Test
	@DisplayName("GET /api/lessons/{id}/download-document - Should download document")
	@WithMockUser(authorities = "DOWNLOAD_LESSON_DOCUMENT")
	void testDownload_Document_Success() throws Exception {
		byte[] data = "test".getBytes();
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

		FileResponse file = FileResponse.builder().fileName("abc.pdf").length(data.length)
				.mediaType(MediaType.APPLICATION_PDF).resource(resource).build();

		when(lessonService.downloadDocumentByLessonId(anyLong())).thenReturn(file);

		mockMvc.perform(get("/api/lessons/1/download-document")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /api/lessons/{id}/download-subfile - Should download subfile")
	@WithMockUser(authorities = "DOWNLOAD_LESSON_SUBFILE")
	void testDownload_SubFile_Success() throws Exception {
		byte[] data = "test".getBytes();
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

		FileResponse file = FileResponse.builder().fileName("abc.zip").length(data.length)
				.mediaType(MediaType.parseMediaType("application/zip")).resource(resource).build();

		when(lessonService.downloadSubFileByLessonId(anyLong())).thenReturn(file);

		mockMvc.perform(get("/api/lessons/1/download-subfile")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST /api/lessons/upload-file - Should upload lesson")
	@WithMockUser(authorities = "UPLOAD_LESSON")
	void testCreate_Success() throws Exception {
		when(lessonService.uploadLesson(any(), any(), any(), any(LessonRequest.class)))
				.thenReturn(lessonDetailResponse);

		String jsonData = objectMapper.writeValueAsString(lessonRequest);
		MockMultipartFile data = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());
		MockMultipartFile video = new MockMultipartFile("video", "moimoi.mp4", "video/mp4", new byte[] { 1, 2, 3, 4 });
		MockMultipartFile document = new MockMultipartFile("document", "moimoi.pdf", "application/pdf",
				new byte[] { 1, 2, 3, 4 });
		MockMultipartFile subfile = new MockMultipartFile("subfile", "archive.zip", "application/zip",
				new byte[] { 1, 2, 3, 4 });

		mockMvc.perform(multipart("/api/lessons/upload-file").file(data).file(video).file(document).file(subfile)
				.contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Lesson"));
	}

	@Test
	@DisplayName("GET /api/lessons/my-lesson - Should get current user lessons")
	@WithMockUser(authorities = "GET_MY_LESSON")
	void testGetMyLesson_Success() throws Exception {
		List<LessonUserResponse> lessons = new ArrayList<>();
		lessons.add(lessonUserResponse);

		when(lessonService.getMyLesson()).thenReturn(lessons);

		mockMvc.perform(get("/api/lessons/my-lesson")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].title").value("Test Lesson"));
	}

	@Test
	@DisplayName("GET /api/lessons/my-lesson/{id} - Should get current user lesson detail")
	@WithMockUser(authorities = "GET_MY_LESSON_DETAIL")
	void testGetMyLessonDetail_Success() throws Exception {
		when(lessonService.getMyLessonDetail(anyLong())).thenReturn(lessonDetailResponse);

		mockMvc.perform(get("/api/lessons/my-lesson/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Lesson"));
	}

	@Test
	@DisplayName("PUT /api/lessons/my-lesson/{id} - Should update lesson")
	@WithMockUser(authorities = "UPDATE_MY_LESSON")
	void testUpdateMyLesson_Success() throws Exception {
		when(lessonService.updateMyLesson(anyLong(), any(LessonRequest.class))).thenReturn(lessonUserResponse);

		mockMvc.perform(put("/api/lessons/my-lesson/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(lessonRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.title").value("Test Lesson"));
	}

	@Test
	@DisplayName("DELETE /api/lessons/my-lesson/{id} - Should delete lesson")
	@WithMockUser(authorities = "DELETE_MY_LESSON")
	void testDeleteMyLesson_Success() throws Exception {
		doNothing().when(lessonService).deleteMyLesson(anyLong());

		mockMvc.perform(delete("/api/lessons/my-lesson/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /api/lessons/my-lesson/count - Should count current user lessons")
	@WithMockUser(authorities = "COUNT_MY_LESSON")
	void testCountMyLesson_Success() throws Exception {
		when(lessonService.countMyLesson()).thenReturn(3L);

		mockMvc.perform(get("/api/lessons/my-lesson/count")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(3L));
	}
}
