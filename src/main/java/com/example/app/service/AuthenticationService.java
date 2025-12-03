package com.example.app.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.AuthenticationRequest;
import com.example.app.dto.request.IntrospectRequest;
import com.example.app.dto.request.LogoutRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.AuthenticationResponse;
import com.example.app.dto.response.IntrospectResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.UserMapper;
import com.example.app.model.InvalidatedToken;
import com.example.app.model.Permission;
import com.example.app.model.Role;
import com.example.app.model.User;
import com.example.app.repository.InvalidatedTokenRepository;
import com.example.app.repository.RoleRepository;
import com.example.app.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository userRepository;
	private final InvalidatedTokenRepository invalidatedTokenRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

	@NonFinal
	@Value("${jwt.secretKey}")
	protected String SIGNER_KEY;

	public AuthenticationResponse login(AuthenticationRequest request) {
		var user = userRepository.findByEmail(request.getEmail());
		if (user == null) {
			throw new RuntimeException("User not found");
		}
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		AuthenticationResponse response = new AuthenticationResponse();
		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
		response.setAuthenticated(authenticated);
		if (authenticated) {
			var token = generateToken(user);
			response.setToken(token);
		}
		return response;
	}

	private String generateToken(User user) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()//
				.subject(user.getUsername())//
				.issuer("moimoi.com")//
				.claim("scope", buildScope(user))//
				.jwtID(UUID.randomUUID().toString())//
				.issueTime(new Date())//
				.expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS))).build();

		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (KeyLengthException e) {
			throw new RuntimeException("KeyLengthException: SIGNER_KEY phải >= 32 bytes");
		} catch (JOSEException e) {
			throw new RuntimeException("JOSEException");
		}
	}

	private String buildScope(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");
		List<Role> roles = user.getRoles();
		for (Role role : roles) {
			stringJoiner.add("ROLE_" + role.getName());
			List<Permission> permissions = role.getPermissions();
			for (Permission permission : permissions) {
				stringJoiner.add(permission.getName());
			}
		}
		return stringJoiner.toString();
	}

	public UserResponse register(UserRequest dto) {
		User user = userMapper.requestToUser(dto);

		List<Role> roles = roleRepository.findAllById(dto.getRoles());
		user.setRoles(roles);

		if (!this.checkEmailExist(dto.getEmail()) && !this.checkUsernameExist(dto.getEmail())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User saved = userRepository.save(user);
			UserResponse response = userMapper.userToResponse(saved);
			return response;
		}
		return null;
	}

	public boolean checkEmailExist(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean checkUsernameExist(String username) {
		return userRepository.existsByUsername(username);
	}

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException, AppException {
		String token = request.getToken();
		boolean isValid = true;
		try {
			verifyToken(token);
		} catch (AppException e) {
			isValid = false;
		}

		IntrospectResponse response = new IntrospectResponse();
		response.setValid(isValid);
		return response;
	}

	public void logout(LogoutRequest request) throws JOSEException, ParseException {

		SignedJWT signToken = verifyToken(request.getToken());
		String jit = signToken.getJWTClaimsSet().getJWTID();
		Date epx = signToken.getJWTClaimsSet().getExpirationTime();

		InvalidatedToken invalidatedToken = new InvalidatedToken();
		invalidatedToken.setId(jit);
		invalidatedToken.setExprityTime(epx);
		invalidatedTokenRepository.save(invalidatedToken);

	}

	public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);

		Date epx = signedJWT.getJWTClaimsSet().getExpirationTime();
		boolean verified = signedJWT.verify(verifier);

		if (!(verified && epx.after(new Date()))) {
			throw new AppException("token khong dung hoac da het han");
		}

		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
			throw new AppException("log out roi khong con hieu luc nua");
		}

		return signedJWT;
	}
}
