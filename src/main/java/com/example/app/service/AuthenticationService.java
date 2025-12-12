package com.example.app.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.AuthenticationRequest;
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
import com.example.app.share.SendMail;
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
	private final PasswordEncoder passwordEncoder;
	private final SendMail sendMail;

	@NonFinal
	@Value("${jwt.secretKey}")
	protected String SIGNER_KEY;

	@Value("${jwt.expirationTime}")
	protected long EXPIRATION_TIME;

	@Value("${jwt.refreshTime}")
	protected long REFRESH_TIME;

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
			String token = generateToken(user);
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
				.expirationTime(Date.from(Instant.now().plus(EXPIRATION_TIME, ChronoUnit.SECONDS))).build();

		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			String token = jwsObject.serialize();
			return token;
		} catch (KeyLengthException e) {
			throw new AppException("secretKey phải >= 32B", 1001, HttpStatus.BAD_REQUEST);
		} catch (JOSEException e) {
			throw new AppException("JOSEException", 1001, HttpStatus.BAD_REQUEST);
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

	public void logout(String token) throws JOSEException, ParseException, AppException {
		try {
			SignedJWT signToken = verifyToken(token);
			String jit = signToken.getJWTClaimsSet().getJWTID();
			Date epx = signToken.getJWTClaimsSet().getExpirationTime();

			InvalidatedToken invalidatedToken = new InvalidatedToken();
			invalidatedToken.setId(jit);
			invalidatedToken.setExprityTime(epx);
			invalidatedTokenRepository.save(invalidatedToken);
		} catch (AppException e) {
			throw new AppException(e.getMessage(), e.getCode(), HttpStatus.BAD_REQUEST);
		}
	}

	public AuthenticationResponse refreshToken(String oldToken) throws JOSEException, ParseException, AppException {
		SignedJWT signToken = verifyToken(oldToken);
		this.logout(oldToken);

		String username = signToken.getJWTClaimsSet().getSubject();
		User user = userRepository.findByUsername(username);

		AuthenticationResponse response = new AuthenticationResponse();
		String token = generateToken(user);
		response.setAuthenticated(true);
		response.setToken(token);

		return response;
	}

	public IntrospectResponse introspect(String token) throws JOSEException, ParseException {
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

	private SignedJWT verifyToken(String token) throws JOSEException, ParseException, AppException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);

		Date epx = signedJWT.getJWTClaimsSet().getExpirationTime();

		boolean verified = signedJWT.verify(verifier);

		if (!epx.after(new Date())) {
			throw new AppException("token đã hết hạn", 1001, HttpStatus.BAD_REQUEST);
		}
		if (!verified) {
			throw new AppException("token không đúng", 1001, HttpStatus.BAD_REQUEST);
		}
		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
			throw new AppException("log out rồi token không còn hiệu lực nữa", 1006, HttpStatus.BAD_REQUEST);
		}

		return signedJWT;
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
		sendMail.sendEmail(saved.getEmail(), activationCode);

		UserResponse response = userMapper.userToResponse(saved);
		return response;
	}

	public void activateAccount(String email, String activationCode) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new AppException("không tìm thấy người dùng", 1001, HttpStatus.BAD_REQUEST);
		}
		if (user.isVerified()) {
			throw new AppException("tài khoản đã kích hoạt rồi", 1001, HttpStatus.BAD_REQUEST);
		}
		if (user.getActivationCode().equals(activationCode)) {
			user.setVerified(true);
			userRepository.save(user);
		} else {
			throw new AppException("mã kích hoạt không đúng", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	private String generateActivationCode() {
		return UUID.randomUUID().toString();
	}

}
