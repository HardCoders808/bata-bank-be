package hardcoders808.bata.bank.backend.service;

import static hardcoders808.bata.bank.backend.security.SecurityConstants.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import hardcoders808.bata.bank.backend.jpa.domain.RefreshToken;
import hardcoders808.bata.bank.backend.jpa.repository.RefreshTokenRepository;
import hardcoders808.bata.bank.backend.model.request.LoginRequest;
import hardcoders808.bata.bank.backend.model.response.LoginResponse;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:08
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final long accessTtlSeconds = 15L * 60L;
    private final long refreshTtlDays = 14L;


    public LoginResponse authenticate(final @NotNull LoginRequest request, final @NotNull HttpServletResponse response) {
        log.info("logging in user {}", request);

        final var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        final var now = LocalDateTime.now();
        final var userId = auth.getName();

        final var deviceId = UUID.randomUUID().toString();
        final var refreshRaw = createRefreshRaw();
        final var refreshHash = sha256Base64Url(refreshRaw);

        final var refreshDoc = RefreshToken.of(
                userId,
                deviceId,
                refreshHash,
                now.plusDays(refreshTtlDays)
        );
        refreshTokenRepository.save(refreshDoc);

        final var roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        final var claims = JwtClaimsSet.builder()
                .subject(userId)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TOKEN_VALID_FOR_SECONDS))
                .claim("roles", roles)
                .build();

        final var accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        setHttpOnlyCookie(response, REFRESH_TOKEN, refreshRaw, Duration.ofDays(refreshTtlDays));
        setHttpOnlyCookie(response, DEVICE_ID, deviceId, Duration.ofDays(refreshTtlDays));

        return new LoginResponse(accessToken, accessTtlSeconds);
    }

    public LoginResponse refreshToken(final HttpServletRequest request, final HttpServletResponse response) {
        final var refreshRaw = getCookieValue(request, REFRESH_TOKEN);
        if (refreshRaw == null) {
            log.info("refresh token not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        final var deviceId = getCookieValue(request, DEVICE_ID);
        if (deviceId == null) {
            log.info("Device Id not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid deviceId");
        }

        final var presentedHash = sha256Base64Url(refreshRaw);
        final var now = LocalDateTime.now();

        final var existing = refreshTokenRepository.findByTokenHash(presentedHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));

        if (existing.getRevokedAt() != null
                || existing.getExpiresAt().isBefore(now)
                || !deviceId.equals(existing.getDeviceId())) {
            log.info("refresh token was requested but did not pass check");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final var newRefreshRaw = createRefreshRaw();
        final var newHash = sha256Base64Url(newRefreshRaw);

        existing.setRevokedAt(now);
        existing.setReplacedByHash(newHash);

        final var replacement = RefreshToken.of(
                existing.getUserId(),
                existing.getDeviceId(),
                newHash,
                now.plusDays(refreshTtlDays)
        );

        refreshTokenRepository.save(existing);
        refreshTokenRepository.save(replacement);

        final var claims = JwtClaimsSet.builder()
                .subject(existing.getUserId())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TOKEN_VALID_FOR_SECONDS))
                .build();

        final var accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        setHttpOnlyCookie(response, REFRESH_TOKEN, newRefreshRaw, Duration.ofDays(refreshTtlDays));
        setHttpOnlyCookie(response, DEVICE_ID, deviceId, Duration.ofDays(refreshTtlDays));

        return new LoginResponse(accessToken, accessTtlSeconds);
    }

    public void logout(final HttpServletRequest request, final HttpServletResponse response) {
        final var refreshRaw = getCookieValue(request, REFRESH_TOKEN);
        final var deviceId = getCookieValue(request, DEVICE_ID);
        final var now = LocalDateTime.now();

        if (refreshRaw != null && deviceId != null) {
            String hash = sha256Base64Url(refreshRaw);
            refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
                rt.setRevokedAt(now);
                refreshTokenRepository.save(rt);
            });
        }

        clearCookie(response, REFRESH_TOKEN);
        clearCookie(response, DEVICE_ID);
    }

    private void clearCookie(final HttpServletResponse response, final String name) {
        final var cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setHttpOnlyCookie(final HttpServletResponse response, final String name, final String value, final Duration maxAge) {
        final var cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String createRefreshRaw() {
        return UUID.randomUUID() + "." + UUID.randomUUID();
    }

    private String sha256Base64Url(final String value) {
        try {
            final var md = MessageDigest.getInstance("SHA-256");
            final var digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String getCookieValue(final HttpServletRequest request, final String name) {
        final var cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}