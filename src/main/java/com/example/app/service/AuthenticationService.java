package com.example.app.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.app.dto.request.AuthenticationRequest;
import com.example.app.dto.request.IntrospectRequest;
import com.example.app.dto.response.AuthenticationResponse;
import com.example.app.dto.response.IntrospectResponse;
import com.example.app.model.User;
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

	@NonFinal
	@Value("${jwt.secretKey}")
	protected String SIGNER_KEY;

	public AuthenticationResponse authenticated(AuthenticationRequest request) {
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

		if (!CollectionUtils.isEmpty(user.getRoles()))
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getName());
				if (!CollectionUtils.isEmpty(role.getPermissions()))
					role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
			});

		return stringJoiner.toString();
	}

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);

		Date epx = signedJWT.getJWTClaimsSet().getExpirationTime();
		var verified = signedJWT.verify(verifier);

		IntrospectResponse response = new IntrospectResponse();
		response.setValid(verified && epx.after(new Date()));
		return response;
	}
}
