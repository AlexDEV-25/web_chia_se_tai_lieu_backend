package com.example.app.configuration;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.app.dto.request.IntrospectRequest;
import com.example.app.dto.response.IntrospectResponse;
import com.example.app.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
	@Value("${jwt.secretKey}")
	private String signerKey;
	private final AuthenticationService authenticationService;
	private NimbusJwtDecoder nimbusJwtDecoder = null;

	@Override
	public Jwt decode(String token) throws JwtException {
		try {
			IntrospectRequest introspectRequest = new IntrospectRequest();
			introspectRequest.setToken(token);
			IntrospectResponse response;
			response = authenticationService.introspect(introspectRequest);
			if (!response.isValid()) {
				throw new JwtException("token invalid");
			}
		} catch (JOSEException | ParseException e) {
			throw new JwtException(e.getMessage());
		}

		if (Objects.isNull(nimbusJwtDecoder)) {
			SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
			nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
		}

		return nimbusJwtDecoder.decode(token);
	}

}
