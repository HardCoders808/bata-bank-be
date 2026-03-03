package hardcoders808.bata.bank.backend.service;

import static hardcoders808.bata.bank.backend.security.SecurityConstants.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
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
import hardcoders808.bata.bank.backend.model.request.MfaVerifyRequest;
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
    private final UserService userService;
    private final MfaService mfaService;


    public LoginResponse verifyMfaAndIssueTokens(final MfaVerifyRequest request,
                                                 final HttpServletRequest httpRequest,
                                                 final HttpServletResponse httpResponse) {

        mfaService.verifyLoginChallenge(request.challengeId(), request.code());

        final var challenge = mfaService.findById(request.challengeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid challenge"));

        final var user = userService.findById(challenge.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!Boolean.TRUE.equals(user.isMfaEnabled())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "MFA not enabled");
        }

        final var now = LocalDateTime.now();

        var deviceId = getCookieValue(httpRequest, DEVICE_ID);
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = UUID.randomUUID().toString();
        }

        final var refreshRaw = createRefreshRaw();
        final var refreshHash = sha256Base64Url(refreshRaw);

        final var refreshDoc = RefreshToken.of(
                user.getEmail(),
                deviceId,
                refreshHash,
                now.plusDays(REFRESH_TTL_DAYS)
        );
        refreshTokenRepository.save(refreshDoc);


        final var claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TOKEN_VALID_FOR_SECONDS))
                .claim("roles", List.of(user.getRole().name()))
                .build();

        final var accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        setHttpOnlyCookie(httpResponse, REFRESH_TOKEN, refreshRaw, Duration.ofDays(REFRESH_TTL_DAYS));
        setHttpOnlyCookie(httpResponse, DEVICE_ID, deviceId, Duration.ofDays(REFRESH_TTL_DAYS));

        return LoginResponse.token(accessToken, TOKEN_VALID_FOR_SECONDS);
    }

    public LoginResponse authenticate(final @NotNull LoginRequest request, final @NotNull HttpServletResponse response) {
        log.info("logging in user {}", request);

        final var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        final var user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (Boolean.TRUE.equals(user.isMfaEnabled())) {
            final var challengeId = mfaService.createLoginChallenge(user.getId());
            setHttpOnlyCookie(response, DEVICE_ID, UUID.randomUUID().toString(), Duration.ofDays(REFRESH_TTL_DAYS));
            return LoginResponse.mfaRequired(challengeId);
        }
        final var now = LocalDateTime.now();
        final var userId = auth.getName();

        final var deviceId = UUID.randomUUID().toString();
        final var refreshRaw = createRefreshRaw();
        final var refreshHash = sha256Base64Url(refreshRaw);

        final var refreshDoc = RefreshToken.of(
                userId,
                deviceId,
                refreshHash,
                now.plusDays(REFRESH_TTL_DAYS)
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

        setHttpOnlyCookie(response, REFRESH_TOKEN, refreshRaw, Duration.ofDays(REFRESH_TTL_DAYS));
        setHttpOnlyCookie(response, DEVICE_ID, deviceId, Duration.ofDays(REFRESH_TTL_DAYS));

        return LoginResponse.token(accessToken, TOKEN_VALID_FOR_SECONDS);
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
                now.plusDays(REFRESH_TTL_DAYS)
        );

        refreshTokenRepository.save(existing);
        refreshTokenRepository.save(replacement);

        final var claims = JwtClaimsSet.builder()
                .subject(existing.getUserId())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TOKEN_VALID_FOR_SECONDS))
                .build();

        final var accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        setHttpOnlyCookie(response, REFRESH_TOKEN, newRefreshRaw, Duration.ofDays(REFRESH_TTL_DAYS));
        setHttpOnlyCookie(response, DEVICE_ID, deviceId, Duration.ofDays(REFRESH_TTL_DAYS));

        return LoginResponse.token(accessToken, TOKEN_VALID_FOR_SECONDS);
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
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setHttpOnlyCookie(final HttpServletResponse response, final String name, final String value, final Duration maxAge) {
        final var cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
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