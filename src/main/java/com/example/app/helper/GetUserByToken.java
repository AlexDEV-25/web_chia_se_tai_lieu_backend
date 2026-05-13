package com.example.app.helper;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.app.model.User;
import com.example.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetUserByToken {

	private final UserRepository userRepository;

	public User get() {

		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return null;
		}

		Authentication authentication = context.getAuthentication();

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken
				|| !authentication.isAuthenticated()) {
			return null;
		}

		String username = authentication.getName();

		if (username == null || username.isBlank() || username.equals("anonymousUser")) {
			return null;
		}

		return userRepository.findByUsername(username);
	}
}