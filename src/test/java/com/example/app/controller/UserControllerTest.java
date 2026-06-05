package com.example.app.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.app.controller.admin.AdminUserController;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.role.RoleResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminUserController.class)
//@WithMockUser(username = "admin", roles = { "ADMIN" })
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;
	private UserRequest request;
	private UserResponse userResponse;
	private final RequestPostProcessor adminJwt = SecurityMockMvcRequestPostProcessors.jwt();

	@BeforeEach
	void initData() {
		request = UserRequest.builder().username("hoangduc").email("hoangduc@gmail.com").password("Password@123")
				.bio("Java Backend Developer").verified(true).roles(List.of("USER", "ADMIN")).hide(false).build();
		userResponse = UserResponse
				.builder().id(1L).username("hoangduc").email("hoangduc@gmail.com").password("$2a$10$abcxyz...")
				.bio("Java Backend Developer").verified(true).avatarUrl("https://example.com/avatar.jpg")
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).roles(List
						.of(RoleResponse.builder().name("USER").build(), RoleResponse.builder().name("ADMIN").build()))
				.hide(false).build();

	}

	@Test
	//
	void createUser_validRequest_success() throws Exception {
		// GIVEN
		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(request);

		Mockito.when(userService.save(ArgumentMatchers.any())).thenReturn(userResponse);

		// WHEN, THEN
		mockMvc.perform(MockMvcRequestBuilders.post("/api/users/admin").with(adminJwt)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000));

	}

	@Test
	//
	void createUser_usernameInvalid_fail() throws Exception {
		// GIVEN
		request.setUsername("j");
		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(request);

		// WHEN, THEN
		mockMvc.perform(MockMvcRequestBuilders.post("/api/users/admin").with(adminJwt)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("message").value("Tên người dùng phải từ 2 đến 50 ký tự"));
	}
}
