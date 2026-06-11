package com.example.app.unit.service;

import com.example.app.constant.AppError;
import com.example.app.dto.request.ActiveAccountRequest;
import com.example.app.dto.request.AuthenticationRequest;
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
import com.example.app.service.AuthenticationService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private OutboundIdentityClient outboundIdentityClient;

    @Mock
    private OutboundUserClient outboundUserClient;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SendMail sendMail;

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("user@example.com")
                .password("Password1!")
                .build();
        User user = User.builder()
                .email("user@example.com")
                .password("encoded-password")
                .verified(true)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(passwordEncoder.matches("Password1!", "encoded-password")).thenReturn(true);
        when(jwtHelper.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.login(request);

        assertEquals(true, response.isAuthenticated());
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("missing@example.com")
                .password("Password1!")
                .build();

        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(request));

        assertEquals(AppError.USER_NOT_FOUND, exception.getAppError());
    }

    @Test
    void login_shouldThrow_whenAccountNotActivated() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("user@example.com")
                .password("Password1!")
                .build();
        User user = User.builder()
                .email("user@example.com")
                .password("encoded-password")
                .verified(false)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(request));

        assertEquals(AppError.ACCOUNT_NOT_ACTIVATED, exception.getAppError());
    }

    @Test
    void login_shouldThrow_whenPasswordInvalid() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("user@example.com")
                .password("wrong-password")
                .build();
        User user = User.builder()
                .email("user@example.com")
                .password("encoded-password")
                .verified(true)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(request));

        assertEquals(AppError.LOGIN_FAILED, exception.getAppError());
    }

    @Test
    void refreshToken_shouldReturnNewToken() throws Exception {
        SignedJWT signedJWT = org.mockito.Mockito.mock(SignedJWT.class);
        JWTClaimsSet claimsSet = org.mockito.Mockito.mock(JWTClaimsSet.class);
        User user = User.builder().username("user01").build();

        when(jwtHelper.verifyToken("old-token")).thenReturn(signedJWT);
        when(signedJWT.getJWTClaimsSet()).thenReturn(claimsSet);
        when(claimsSet.getSubject()).thenReturn("user01");
        when(userRepository.findByUsername("user01")).thenReturn(user);
        when(jwtHelper.generateToken(user)).thenReturn("new-token");

        AuthenticationResponse response = authenticationService.refreshToken("old-token");

        assertEquals(true, response.isAuthenticated());
        assertEquals("new-token", response.getToken());
    }

    @Test
    void refreshToken_shouldThrow_whenParseError() throws Exception {
        when(jwtHelper.verifyToken("old-token")).thenThrow(new ParseException("bad data", 0));

        AppException exception = assertThrows(AppException.class,
                () -> authenticationService.refreshToken("old-token"));

        assertEquals(AppError.FAILED_TO_PARSE_DATA, exception.getAppError());
    }

    @Test
    void introspect_shouldReturnResponse() throws Exception {
        IntrospectResponse expected = new IntrospectResponse();
        when(jwtHelper.introspect("token")).thenReturn(expected);

        IntrospectResponse response = authenticationService.introspect("token");

        assertEquals(expected, response);
    }

    @Test
    void register_shouldSaveUserAndSendActivationMail() {
        UserRequest request = UserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("Password1!")
                .roles(List.of("USER"))
                .hide(false)
                .build();
        User mappedUser = User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("Password1!")
                .build();
        Role role = Role.builder().name("USER").build();
        User savedUser = User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("encoded-password")
                .activationCode("activation-code")
                .build();
        UserResponse expectedResponse = new UserResponse();

        when(userMapper.requestToUser(request)).thenReturn(mappedUser);
        when(roleRepository.findAllById(List.of("USER"))).thenReturn(List.of(role));
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.userToResponse(savedUser)).thenReturn(expectedResponse);
        doNothing().when(sendMail).sendEmailActivateAccount(eq("newuser@example.com"), anyString());

        UserResponse response = authenticationService.register(request);

        assertEquals(expectedResponse, response);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
        assertNotNull(userCaptor.getValue().getActivationCode());
        assertEquals("newuser@example.com", userCaptor.getValue().getEmail());
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        UserRequest request = UserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("Password1!")
                .roles(List.of("USER"))
                .hide(false)
                .build();
        when(userMapper.requestToUser(request)).thenReturn(User.builder().email("newuser@example.com").build());
        when(roleRepository.findAllById(List.of("USER"))).thenReturn(List.of());
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.register(request));

        assertEquals(AppError.EMAIL_ALREADY_EXISTS, exception.getAppError());
    }

    @Test
    void activateAccount_shouldActivateUser() {
        User user = User.builder()
                .email("user@example.com")
                .verified(false)
                .activationCode("abc123")
                .build();
        ActiveAccountRequest request = ActiveAccountRequest.builder()
                .email("user@example.com")
                .activationCode("abc123")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        authenticationService.activateAccount(request);

        assertEquals(true, user.isVerified());
        assertNull(user.getActivationCode());
        verify(userRepository).save(user);
    }

    @Test
    void activateAccount_shouldThrow_whenCodeInvalid() {
        User user = User.builder()
                .email("user@example.com")
                .verified(false)
                .activationCode("abc123")
                .build();
        ActiveAccountRequest request = ActiveAccountRequest.builder()
                .email("user@example.com")
                .activationCode("wrong")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.activateAccount(request));

        assertEquals(AppError.INVALID_VERIFICATION_CODE, exception.getAppError());
    }

    @Test
    void forgotPassword_shouldGenerateCodeAndSendMail() {
        User user = User.builder().email("user@example.com").build();
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(sendMail).sendEmailChangePassword(anyString(), anyString());

        authenticationService.forgotPassword("user@example.com");

        assertNotNull(user.getForgotPasswordCode());
        verify(sendMail).sendEmailChangePassword(eq("user@example.com"), anyString());
    }

    @Test
    void forgotPassword_shouldThrow_whenEmailNotFound() {
        when(userRepository.existsByEmail("missing@example.com")).thenReturn(false);

        AppException exception = assertThrows(AppException.class,
                () -> authenticationService.forgotPassword("missing@example.com"));

        assertEquals(AppError.EMAIL_NOT_FOUND, exception.getAppError());
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        User user = User.builder()
                .email("user@example.com")
                .forgotPasswordCode("forgot-code")
                .build();
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("user@example.com")
                .forgotPasswordCode("forgot-code")
                .password("Password1!")
                .build();
        User savedUser = User.builder().email("user@example.com").build();
        UserResponse expectedResponse = new UserResponse();

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(passwordEncoder.encode("Password1!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.userToResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse response = authenticationService.changePassword(request);

        assertEquals(expectedResponse, response);
        assertNull(user.getForgotPasswordCode());
        assertEquals("encoded-password", user.getPassword());
    }

    @Test
    void changePassword_shouldThrow_whenCodeInvalid() {
        User user = User.builder()
                .email("user@example.com")
                .forgotPasswordCode("forgot-code")
                .build();
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("user@example.com")
                .forgotPasswordCode("wrong")
                .password("Password1!")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.changePassword(request));

        assertEquals(AppError.INVALID_VERIFICATION_CODE, exception.getAppError());
    }

    @Test
    void loginWithGoogle_shouldCreateUserWhenNotExist() {
        ExchangeTokenResponse exchangeTokenResponse = new ExchangeTokenResponse();
        exchangeTokenResponse.setAccessToken("access-token");
        OutboudUserResponse userInfo = new OutboudUserResponse();
        userInfo.setEmail("google@example.com");
        Role role = Role.builder().name("USER").build();
        User savedUser = User.builder().email("google@example.com").build();

        when(outboundIdentityClient.exchangeToken(any())).thenReturn(exchangeTokenResponse);
        when(outboundUserClient.getUserDetails("Bearer access-token")).thenReturn(userInfo);
        when(userRepository.existsByEmail("google@example.com")).thenReturn(false);
        when(roleRepository.findAllById(List.of("USER"))).thenReturn(List.of(role));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtHelper.generateToken(savedUser)).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.loginWithGoogle("code");

        assertEquals(true, response.isAuthenticated());
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginWithGoogle_shouldReturnTokenForExistingUser() {
        ExchangeTokenResponse exchangeTokenResponse = new ExchangeTokenResponse();
        exchangeTokenResponse.setAccessToken("access-token");
        OutboudUserResponse userInfo = new OutboudUserResponse();
        userInfo.setEmail("google@example.com");
        User existingUser = User.builder().email("google@example.com").build();

        when(outboundIdentityClient.exchangeToken(any())).thenReturn(exchangeTokenResponse);
        when(outboundUserClient.getUserDetails("Bearer access-token")).thenReturn(userInfo);
        when(userRepository.existsByEmail("google@example.com")).thenReturn(true);
        when(userRepository.findByEmail("google@example.com")).thenReturn(existingUser);
        when(jwtHelper.generateToken(existingUser)).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.loginWithGoogle("code");

        assertEquals(true, response.isAuthenticated());
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginWithGoogle_shouldThrow_whenAnyStepFails() {
        when(outboundIdentityClient.exchangeToken(any())).thenThrow(new RuntimeException("boom"));

        AppException exception = assertThrows(AppException.class, () -> authenticationService.loginWithGoogle("code"));

        assertEquals(AppError.GOOGLE_LOGIN_FAILED, exception.getAppError());
    }

}
