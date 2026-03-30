package io.athos.agrocore.plantmonitor.security;

import io.athos.agrocore.plantmonitor.errors.InvalidInputRefreshToken;
import io.athos.agrocore.plantmonitor.errors.InvalidRefreshTokenException;
import io.athos.agrocore.plantmonitor.security.dtos.AuthRegisterResponse;
import io.athos.agrocore.plantmonitor.security.dtos.AuthTokenResponse;
import io.athos.agrocore.plantmonitor.security.dtos.AuthUpdateRequest;
import io.athos.agrocore.plantmonitor.security.dtos.RegisterAuthRequest;
import io.athos.agrocore.plantmonitor.users.User;
import io.athos.agrocore.plantmonitor.users.UserToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import io.athos.agrocore.plantmonitor.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {
    @Autowired
    public UserService userService;
    @Autowired
    JwtService jwtService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthTokenResponse generateTokenPair(UserDetails userDetails){
        final String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
        final String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setJti(jwtService.extractJti(refreshToken));
        refreshTokenEntity.setUsername(userDetails.getUsername());
        refreshTokenEntity.setExpiryDate(jwtService.extractExpirationDate(refreshToken));
        refreshTokenEntity.setBlacklisted(false);
        refreshTokenRepository.save(refreshTokenEntity);
        return new AuthTokenResponse(accessToken, refreshToken, "bearer");
    }


    public User registerUser(RegisterAuthRequest registerRequest) {
        User user = userService.createUser(registerRequest, passwordEncoder.encode(registerRequest.password()));
//        generateTokenAndSendEmail(user, UserToken.UserTokenType.ACTIVATION);
        return user;
    }
    public  AuthTokenResponse login(LoginAuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateTokenPair(Objects.requireNonNull(userDetails));
    }

    public  AuthTokenResponse refreshToken(String refreshToken) {
        return new AuthTokenResponse(null, null, "Bearer");

    }

    void logout(String refreshToken) {
        if (jwtService.isAccessToken(refreshToken)) {
            throw new InvalidInputRefreshToken();
        }
        String jti = jwtService.extractJti(refreshToken);
        RefreshToken refreshTokenEntity = getRefreshTokenByJti(jti);
        if (refreshTokenEntity.isBlacklisted()) {
            throw new InvalidRefreshTokenException("Refresh token já foi invalidado anteriormente.");
        }
        refreshTokenEntity.setBlacklisted(true);
        refreshTokenRepository.save(refreshTokenEntity);
    }

    private RefreshToken getRefreshTokenByJti(String jti) {
        return refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Refresh token não existe ou já foi removido. Faça login novamente."
                ));
    }
    public User updateUser(SecurityUser user, AuthUpdateRequest request) {
        return userService.updateUser(user.getPersistentUser(), request);
    }

}
