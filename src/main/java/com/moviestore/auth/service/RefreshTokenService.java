package com.moviestore.auth.service;

import com.moviestore.auth.entities.RefreshToken;
import com.moviestore.auth.entities.User;
import com.moviestore.auth.repositories.RefreshTokenRepository;
import com.moviestore.auth.repositories.UserRepository;
import com.moviestore.exception.RefreshTokenExpiredException;
import com.moviestore.exception.RefreshTokenNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username){
        User user = userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User Not found with email: " + username));

        RefreshToken refreshToken = user.getRefreshToken();

        if(refreshToken == null){
//            long refreshTokenValidity = 5*60*60*10000;
            long refreshTokenValidity = 30*1000; // 30 Sec
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public  RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new RefreshTokenNotFoundException("Refresh Token Not Found!"));

        if(refToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refToken);
            throw new RefreshTokenExpiredException("Refresh Token Expired! Please login again.");
        }
        return refToken;
    }

}
