package com.example.app.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.lesson.LessonAdminResponse;
import com.example.app.dto.response.lesson.LessonDetailResponse;
import com.example.app.service.LessonService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/lessons/admin")
@AllArgsConstructor
public class AdminLessonController {

	private final LessonService lessonService;

	@GetMapping("/{id}")
	public APIResponse<LessonDetailResponse> getById(@PathVariable Long id) {
		LessonDetailResponse response = lessonService.findById(id);
		APIResponse<LessonDetailResponse> apiResponse = new APIResponse<LessonDetailResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<LessonAdminResponse> getAll() {
		List<LessonAdminResponse> response = lessonService.getAllLessons();
		APIResponse<LessonAdminResponse> apiResponse = new APIResponse<LessonAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<Void> delete(@PathVariable Long id) {
		lessonService.delete(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<LessonDetailResponse> update(@PathVariable Long id, @RequestBody LessonRequest dto) {
		LessonDetailResponse response = lessonService.update(id, dto);
		APIResponse<LessonDetailResponse> apiResponse = new APIResponse<LessonDetailResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

}
