package com.airflights.auth.unit;

import com.airflights.auth.dto.LoginRequest;
import com.airflights.auth.dto.TokenResponse;
import com.airflights.auth.security.JwtService;
import com.airflights.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_returnsTokenResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token-value");
        when(jwtService.getExpirationMs()).thenReturn(3600L);

        TokenResponse response = authService.login(request);

        assertEquals("token-value", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("user1", captor.getValue().getName());
    }
}
