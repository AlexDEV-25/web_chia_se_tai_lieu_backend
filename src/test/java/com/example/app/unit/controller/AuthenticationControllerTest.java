package com.example.app.unit.controller;

import com.example.app.dto.request.ActiveAccountRequest;
import com.example.app.dto.request.AuthenticationRequest;
import com.example.app.dto.request.ForgotPasswordRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.authentication.AuthenticationResponse;
import com.example.app.dto.response.authentication.IntrospectResponse;
import com.example.app.dto.response.role.RoleResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("AuthenticationController Tests")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;
    private UserRequest userRequest;
    private UserResponse userResponse;

    private String token;

    @BeforeEach
    void setUp() {
        // Initialize test data
        authenticationRequest = AuthenticationRequest.builder().email("test@example.com").password("Password123!")
                .build();

        authenticationResponse = AuthenticationResponse.builder().authenticated(true).token("test-token-123").build();

        userRequest = UserRequest.builder().email("test@example.com").username("testuser").password("Password123!")
                .bio("Test bio").verified(false).roles(Arrays.asList("USER")).hide(false).build();

        userResponse = UserResponse.builder().id(1L).email("test@example.com").username("testuser").verified(false)
                .avatarUrl(null).roles(Arrays.asList(new RoleResponse())).hide(false).build();

        token = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJtb2ltb2kuY29tIiwic3ViIjoibmfGsOG7nWkgZMO5bmcgMSIsImV4cCI6MTc4MTA3MDI2OSwiaWF0IjoxNzgxMDY2NjY5LCJqdGkiOiIwYjEwYzQ2MS05MjE5LTRjODUtOTJhNy0yMjQ4NGU3ZTZmNTUiLCJzY29wZSI6IlJPTEVfVVNFUiBBRERfRkFWT1JJVEUgQUREX01FTUJFUiBDSEFOR0VfUEFTU1dPUkQgQ0hBTkdFX1JPTEUgQ0hBVF9HRU1JTkkgQ0hFQ0tfRE9DVU1FTlRfRkFWT1JJVEUgQ0hFQ0tfRk9MTE9XRUQgQ0hFQ0tfSVNfTUUgQ0hFQ0tfTEVTU09OX0ZBVk9SSVRFIENPVU5UX01ZX0RPQ1VNRU5UIENPVU5UX01ZX0xFU1NPTiBDUkVBVEVfRElSRUNUX0NPTlZFUlNBVElPTiBDUkVBVEVfR1JPVVBfQ09OVkVSU0FUSU9OIENSRUFURV9NRVNTQUdFIERFTEVURV9NRU1CRVIgREVMRVRFX01ZX0RPQ1VNRU5UIERFTEVURV9NWV9MRVNTT04gRE9XTkxPQURfRklMRSBET1dOTE9BRF9MRVNTT05fRE9DVU1FTlQgRE9XTkxPQURfTEVTU09OX1NVQkZJTEUgRk9MTE9XIEdFVF9BTExfVVNFUl9OT1RJRklDQVRJT04gR0VUX0RFVEFJTF9DT05WRVJTQVRJT04gR0VUX0RPQ1VNRU5UX0ZBVk9SSVRFIEdFVF9MRVNTT05fRkFWT1JJVEUgR0VUX0xJU1RfRk9MTE9XRVIgR0VUX0xJU1RfRk9MTE9XSU5HIEdFVF9NRVNTQUdFIEdFVF9NWV9DT05WRVJTQVRJT04gR0VUX01ZX0RPQ1VNRU5UIEdFVF9NWV9ET0NVTUVOVF9ERVRBSUwgR0VUX01ZX0RPQ1VNRU5UX1JBVElORyBHRVRfTVlfRk9MTE9XX0NPVU5UIEdFVF9NWV9JTkZPIEdFVF9NWV9MRVNTT04gR0VUX01ZX0xFU1NPTl9ERVRBSUwgR0VUX01ZX0xFU1NPTl9SQVRJTkcgR0VUX1VOUkVBRF9VU0VSX05PVElGSUNBVElPTiBISVNUT1JZX0NIQVRfR0VNSU5JIFBPU1RfQ09NTUVOVCBQT1NUX1JBVElORyBSRUFEX0FMTF9OT1RJRklDQVRJT04gUkVBRF9OT1RJRklDQVRJT04gUkVNT1ZFX0RPQ1VNRU5UX0ZBVk9SSVRFIFJFTU9WRV9MRVNTT05fRkFWT1JJVEUgUkVQT1JUIFNFQVJDSF9DT05WRVJTQVRJT04gU0VBUkNIX1VTRVIgVU5GT0xMT1cgVVBEQVRFX0xBU1RfU0VFTiBVUERBVEVfTVlfQ09NTUVOVCBVUERBVEVfTVlfRE9DVU1FTlQgVVBEQVRFX01ZX0lORk8gVVBEQVRFX01ZX0xFU1NPTiBVUExPQURfRklMRSBVUExPQURfTEVTU09OIn0.AdSmCw1PbiyWgqZ2k1No2gvfEm7HYQrQnjHXR2LQ2_3drys3VycY1OWayq7zcNyltKnxKzPNZgCpZLxuVhiTXQ";
    }

    @Test
    @DisplayName("POST /api/auth/log-in - Should login successfully")
    void testLogin_Success() throws Exception {
        when(authenticationService.login(any(AuthenticationRequest.class))).thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/auth/log-in").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.authenticated").value(true))
                .andExpect(jsonPath("$.result.token").value("test-token-123"));
    }

    @Test
    @DisplayName("POST /api/auth/log-in-google - Should login with Google successfully")
    void testLoginWithGoogle_Success() throws Exception {
        String googleCode = "google-code-123";

        when(authenticationService.loginWithGoogle(googleCode)).thenReturn(authenticationResponse);

        mockMvc.perform(
                        post("/api/auth/log-in-google").param("code", googleCode).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.result.token").value("test-token-123"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should register user successfully")
    void testRegister_Success() throws Exception {
        when(authenticationService.register(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /api/auth/activate - Should activate account successfully")
    void testActivateAccount_Success() throws Exception {
        ActiveAccountRequest request = ActiveAccountRequest.builder().email("test@example.com").activationCode("123456")
                .build();

        mockMvc.perform(post("/api/auth/activate").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password - Should send forgot password email")
    void testForgotPassword_Success() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post("/api/auth/forgot-password").param("email", email).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/change-password - Should change password successfully")
    void testChangePassword_Success() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder().email("test@example.com")
                .forgotPasswordCode("123456").password("newpassword123").build();

        when(authenticationService.changePassword(any(ForgotPasswordRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/auth/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh-token - Should refresh token successfully")
    void testRefreshToken_Success() throws Exception {

        when(authenticationService.refreshToken(any(String.class))).thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/auth/refresh-token").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token").value("test-token-123"));

        verify(authenticationService).refreshToken(token);
    }

    @Test
    @DisplayName("POST /api/auth/introspect - Should introspect token successfully")
    void testIntrospect_Success() throws Exception {

        when(authenticationService.introspect(any(String.class)))
                .thenReturn(IntrospectResponse.builder().valid(true).build());

        mockMvc.perform(post("/api/auth/introspect").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(authenticationService).introspect(token);
    }
}
