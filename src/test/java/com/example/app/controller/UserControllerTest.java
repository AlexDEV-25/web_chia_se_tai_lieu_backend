package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.app.dto.request.ChangePasswordRequest;
import com.example.app.dto.request.ChangeUserInfoRequest;
import com.example.app.dto.response.user.UserBioResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("UserController Tests")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private UserResponse userResponse;
	private UserBioResponse userBioResponse;
	private ChangeUserInfoRequest changeUserInfoRequest;
	private ChangePasswordRequest changePasswordRequest;

	@BeforeEach
	void setUp() {
		userResponse = UserResponse.builder().id(1L).email("test@example.com").username("testuser").verified(false)
				.avatarUrl("http://example.com/avatar.jpg").build();

		userBioResponse = UserBioResponse.builder().id(1L).username("testuser")
				.avatarUrl("http://example.com/avatar.jpg").bio("Test bio").build();

		changeUserInfoRequest = ChangeUserInfoRequest.builder().username("Updated User").email("updated@example.com")
				.bio("Updated bio").build();

		changePasswordRequest = ChangePasswordRequest.builder().password("Testmatkhau@2345").build();
	}

	@Test
	@DisplayName("GET /api/users/info/{id} - Should get user info by id")
	void testGetInfo_Success() throws Exception {
		Long userId = 1L;
		when(userService.getUserInfo(userId)).thenReturn(userBioResponse);

		mockMvc.perform(get("/api/users/info/" + userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.username").value("testuser"));
	}

	@Test
	@DisplayName("GET /api/users/my-info - Should get current user info")
	@WithMockUser(authorities = "GET_MY_INFO")
	void testGetMyInfo_Success() throws Exception {
		when(userService.getMyInfo()).thenReturn(userResponse);

		mockMvc.perform(get("/api/users/my-info")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.email").value("test@example.com"))
				.andExpect(jsonPath("$.result.username").value("testuser"));
	}

	@Test
	@DisplayName("PUT /api/users/my-info - Should update user info with multipart")
	@WithMockUser(authorities = "UPDATE_MY_INFO")
	void testUpdateMyInfo_Success() throws Exception {
		when(userService.updateMyinfo(any(), any(ChangeUserInfoRequest.class))).thenReturn(userResponse);

		String jsonData = objectMapper.writeValueAsString(changeUserInfoRequest);

		MockMultipartFile data = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());
		MockMultipartFile avt = new MockMultipartFile("avt", "moimoi.pdf", "image/png", new byte[] { 1, 2, 3, 4 });

		mockMvc.perform(multipart("/api/users/my-info").file(data).file(avt).with(request -> {
			request.setMethod("PUT"); // override method thành PUT
			return request;
		}).contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.username").value("testuser"));
	}

	@Test
	@DisplayName("PUT /api/users/change-password - Should change password successfully")
	@WithMockUser(authorities = "CHANGE_PASSWORD")
	void testChangePassword_Success() throws Exception {
		mockMvc.perform(put("/api/users/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(changePasswordRequest))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /api/users/search - Should search users by keyword")
	@WithMockUser(authorities = "SEARCH_USER")
	void testSearch_Success() throws Exception {
		List<UserBioResponse> searchResults = new ArrayList<>();
		searchResults.add(userBioResponse);

		when(userService.search(anyString())).thenReturn(searchResults);

		mockMvc.perform(get("/api/users/search").param("keyword", "testuser")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].username").value("testuser"));
	}

	@Test
	@DisplayName("GET /api/users/email/{email} - Should check if email exists")
	void testCheckEmailExist_True() throws Exception {
		when(userService.checkEmailExists(anyString())).thenReturn(true);

		mockMvc.perform(get("/api/users/email/test@example.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(true));
	}

	@Test
	@DisplayName("GET /api/users/email/{email} - Should return false when email not exists")
	void testCheckEmailExist_False() throws Exception {
		when(userService.checkEmailExists(anyString())).thenReturn(false);

		mockMvc.perform(get("/api/users/email/notexist@example.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(false));
	}

	@Test
	@DisplayName("GET /api/users/username/{username} - Should check if username exists")
	void testCheckUsernameExists_True() throws Exception {
		when(userService.checkUsernameExists(anyString())).thenReturn(true);

		mockMvc.perform(get("/api/users/username/testuser")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(true));
	}

	@Test
	@DisplayName("GET /api/users/username/{username} - Should return false when username not exists")
	void testCheckUsernameExists_False() throws Exception {
		when(userService.checkUsernameExists(anyString())).thenReturn(false);

		mockMvc.perform(get("/api/users/username/notexist")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(false));
	}
}
