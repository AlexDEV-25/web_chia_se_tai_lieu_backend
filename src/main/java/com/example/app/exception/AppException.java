package com.example.app.exception;

import org.springframework.http.HttpStatusCode;

public class AppException extends RuntimeException {
	private int code;
	private HttpStatusCode statusCode;

	public AppException(String message, int code, HttpStatusCode statusCode) {
		super(message);
		this.code = code;
		this.statusCode = statusCode;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.statusCode = statusCode;
	}

}
