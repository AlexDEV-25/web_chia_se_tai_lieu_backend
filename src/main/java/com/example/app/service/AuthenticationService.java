package com.example.app.service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.ActiveAccountRequest;
import com.example.app.dto.request.AuthenticationRequest;
import com.example.app.dto.request.ExchangeTokenRequest;
import com.example.app.dto.request.ForgotPasswordRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.authentication.AuthenticationResponse;
import com.example.app.dto.response.authentication.ExchangeTokenResponse;
import com.example.app.dto.response.authentication.IntrospectResponse;
import com.example.app.dto.response.authentication.OutboudUserResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.JwtHelper;
import com.example.app.helper.SendMail;
import com.example.app.mapper.UserMapper;
import com.example.app.model.Role;
import com.example.app.model.User;
import com.example.app.repository.RoleRepository;
import com.example.app.repository.UserRepository;
import com.example.app.repository.httpclient.OutboundIdentityClient;
import com.example.app.repository.httpclient.OutboundUserClient;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final OutboundIdentityClient outboundIdentityClient;
	private final OutboundUserClient outboundUserClient;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final SendMail sendMail;
	private final JwtHelper jwtHelper;

	@Value("${outbound.identity.client-id}")
	protected String CLIENT_ID;

	@Value("${outbound.identity.client-secret}")
	protected String CLIENT_SECRET;

	@Value("${outbound.identity.redirect-uri}")
	protected String REDIRECT_URI;

	@Value("${outbound.identity.grant-types}")
	protected String GRANT_TYPES;

	public AuthenticationResponse login(AuthenticationRequest request) {
		User user = userRepository.findByEmail(request.getEmail());
		if (user == null) {
			throw new AppException("không tìm thấy người dùng", 1001, HttpStatus.BAD_REQUEST);
		}
		if (user.isVerified() == false) {
			throw new AppException("tài khoản chưa kích hoạt", 1001, HttpStatus.BAD_REQUEST);
		}
		AuthenticationResponse response = new AuthenticationResponse();
		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
		response.setAuthenticated(authenticated);
		if (authenticated) {
			String token = jwtHelper.generateToken(user);
			response.setToken(token);
		}
		return response;
	}

	public AuthenticationResponse refreshToken(String oldToken) throws JOSEException, ParseException, AppException {
		SignedJWT signToken = jwtHelper.verifyToken(oldToken);

		String username = signToken.getJWTClaimsSet().getSubject();
		User user = userRepository.findByUsername(username);

		AuthenticationResponse response = new AuthenticationResponse();
		String token = jwtHelper.generateToken(user);
		response.setAuthenticated(true);
		response.setToken(token);

		return response;
	}

	public IntrospectResponse introspect(String token) throws JOSEException, ParseException {
		return jwtHelper.introspect(token);
	}

	public UserResponse register(UserRequest request) {
		User user = userMapper.requestToUser(request);

		String activationCode = this.generateActivationCode();

		List<Role> roles = roleRepository.findAllById(request.getRoles());
		user.setRoles(roles);
		user.setCreatedAt(LocalDateTime.now());
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new AppException("email đã tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
		if (userRepository.existsByUsername(request.getEmail())) {
			throw new AppException("username đã tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setActivationCode(activationCode);

		User saved = userRepository.save(user);
		sendMail.sendEmailActivateAccount(saved.getEmail(), activationCode);

		UserResponse response = userMapper.userToResponse(saved);
		return response;
	}

	public void activateAccount(ActiveAccountRequest request) {
		User user = userRepository.findByEmail(request.getEmail());
		if (user == null) {
			throw new AppException("không tìm thấy người dùng", 1001, HttpStatus.BAD_REQUEST);
		}
		if (user.isVerified()) {
			throw new AppException("tài khoản đã kích hoạt rồi", 1001, HttpStatus.BAD_REQUEST);
		}
		if (user.getActivationCode().equals(request.getActivationCode())) {
			user.setVerified(true);
			user.setActivationCode(null);
			userRepository.save(user);
		} else {
			throw new AppException("mã kích hoạt không đúng", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public void forgotPassword(String email) {
		if (userRepository.existsByEmail(email)) {
			User user = userRepository.findByEmail(email);
			String forgotPassWordCode = this.generateActivationCode();
			user.setForgotPasswordCode(forgotPassWordCode);
			userRepository.save(user);
			sendMail.sendEmailChangePassword(email, forgotPassWordCode);
		} else {
			throw new AppException("email không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public UserResponse changePassword(ForgotPasswordRequest request) {
		User user = userRepository.findByEmail(request.getEmail());
		if (user == null) {
			throw new AppException("không tìm thấy người dùng", 1001, HttpStatus.BAD_REQUEST);
		}

		if (user.getForgotPasswordCode().equals(request.getForgotPasswordCode())) {
			user.setForgotPasswordCode(null);
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			user.setUpdatedAt(LocalDateTime.now());
			User saved = userRepository.save(user);
			UserResponse response = userMapper.userToResponse(saved);
			return response;
		} else {
			throw new AppException("mã không đúng", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public AuthenticationResponse loginWithGoogle(String code) {
		ExchangeTokenRequest request = new ExchangeTokenRequest(code, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI,
				GRANT_TYPES);
		AuthenticationResponse response = new AuthenticationResponse();
		try {
			ExchangeTokenResponse exchangeTokenResponse = outboundIdentityClient.exchangeToken(request);

			String bearerToken = "Bearer " + exchangeTokenResponse.getAccessToken();
			OutboudUserResponse userInfo = outboundUserClient.getUserDetails(bearerToken);

			boolean exist = userRepository.existsByEmail(userInfo.getEmail());

			String token = "";
			if (exist == false) {
				List<String> Stringroles = new ArrayList<String>();
				Stringroles.add("USER");
				List<Role> roles = roleRepository.findAllById(Stringroles);

				User newUser = User.builder().username(userInfo.getEmail()).email(userInfo.getEmail()).verified(true)
						.hide(false).roles(roles).build();
				User save = userRepository.save(newUser);
				token = jwtHelper.generateToken(save);
			} else {
				User user = userRepository.findByEmail(userInfo.getEmail());
				token = jwtHelper.generateToken(user);
			}

			response.setToken(token);
			response.setAuthenticated(true);
		} catch (Exception e) {
			throw new AppException(e.getMessage(), 1001, HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	private String generateActivationCode() {
		return UUID.randomUUID().toString();
	}

}
