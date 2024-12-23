package com.moviestore.controller;

import com.moviestore.auth.entities.RefreshToken;
import com.moviestore.auth.entities.User;
import com.moviestore.auth.service.JwtService;
import com.moviestore.auth.service.RefreshTokenService;
import com.moviestore.auth.utils.AuthResponse;
import com.moviestore.auth.utils.LoginRequest;
import com.moviestore.auth.utils.RefreshTokenRequest;
import com.moviestore.auth.utils.RegisterRequest;
import com.moviestore.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register-user")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest registerRequest){

        return ResponseEntity.ok(authService.registerAsUser(registerRequest));
    }
    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestBody RegisterRequest registerRequest){

        return ResponseEntity.ok(authService.registerAsAdmin(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){

        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest registerRequest){

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(registerRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build() );
    }
}
