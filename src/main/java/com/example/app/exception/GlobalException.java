package com.example.app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.app.dto.response.APIResponse;

@ControllerAdvice
public class GlobalException {

	@ExceptionHandler(RuntimeException.class)
	ResponseEntity<APIResponse<String>> handlingRuntimeException(RuntimeException e) {
		APIResponse<String> error = new APIResponse<String>();
		error.setCode(9999);
		error.setMessage("lỗi khi chạy");
		return ResponseEntity.status(400).body(error);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<APIResponse<String>> handlingException(Exception e) {
		APIResponse<String> error = new APIResponse<String>();
		error.setCode(9999);
		error.setMessage("lỗi khi tạo");
		return ResponseEntity.status(400).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<APIResponse<String>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		APIResponse<String> error = new APIResponse<String>();
		error.setCode(9999);
		error.setMessage("lỗi validate");
		return ResponseEntity.status(400).body(error);
	}

	@ExceptionHandler(AppException.class)
	ResponseEntity<APIResponse<AppException>> handlingAppException(AppException e) {
		APIResponse<AppException> error = new APIResponse<>();
		error.setCode(e.getCode());
		error.setMessage(e.getMessage());
		return ResponseEntity.status(e.getStatusCode()).body(error);
	}

}
