package com.example.app.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.app.dto.response.authentication.OutboudUserResponse;

@FeignClient(name = "outbound-user-client", url = "https://www.googleapis.com")
public interface OutboundUserClient {

	@GetMapping("/oauth2/v2/userinfo")
	OutboudUserResponse getUserDetails(@RequestHeader("Authorization") String authorization);
}
