package hardcoders808.bata.bank.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import hardcoders808.bata.bank.backend.jpa.domain.MfaChallenge;
import hardcoders808.bata.bank.backend.jpa.domain.UserMfa;
import hardcoders808.bata.bank.backend.jpa.repository.MfaChallengeRepository;
import hardcoders808.bata.bank.backend.jpa.repository.UserMfaRepository;
import hardcoders808.bata.bank.backend.jpa.repository.UserRepository;
import hardcoders808.bata.bank.backend.model.request.MfaConfirmRequest;
import hardcoders808.bata.bank.backend.model.response.MfaEnrollResponse;
import hardcoders808.bata.bank.backend.security.mfa.AesGcmCrypto;
import hardcoders808.bata.bank.backend.security.mfa.TotpService;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 20:10
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MfaService {
    private final UserRepository userRepository;
    private final UserMfaRepository userMfaRepository;
    private final MfaChallengeRepository challengeRepository;

    private final TotpService totpService;
    private final AesGcmCrypto crypto;

    private static final String issuer = "BataBank";

    @Transactional
    public MfaEnrollResponse enrollTotp(final Authentication authentication) {
        final var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return enrollTotp(user.getId());
    }

    @Transactional
    public MfaEnrollResponse enrollTotp(final long userId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        final var secret = totpService.generateSecret();
        final var encrypted = crypto.encrypt(secret);


        final var now = LocalDateTime.now();
        final var mfa = UserMfa.builder()
                .user(user)
                .secret(encrypted)
                .enabled(false)
                .createdAt(now)
                .build();

        mfa.setUser(user);

        userMfaRepository.save(mfa);

        final var url = totpService.buildOtpAuthUrl(issuer, user.getEmail(), secret);

        return new MfaEnrollResponse(url, mask(secret));
    }


    @Transactional
    public void confirmEnrollment(final MfaConfirmRequest request, final Authentication authentication) {
        final var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        final var mfa = userMfaRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "MFA not enrolled"));

        final var secret = crypto.decrypt(mfa.getSecret());

        if (!totpService.verify(secret, request.code())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP");
        }

        mfa.setEnabled(true);

        userMfaRepository.save(mfa);
        userRepository.save(user);
    }

    @Transactional
    public String createLoginChallenge(final long userId) {
        final var id = UUID.randomUUID().toString();
        final var now = LocalDateTime.now();

        final var challenge = MfaChallenge.builder()
                .id(id)
                .userId(userId)
                .purpose("LOGIN")
                .expiresAt(now.plusMinutes(2))
                .attempts(0)
                .verifiedAt(null)
                .createdAt(now)
                .build();

        challengeRepository.save(challenge);
        return id;
    }

    @Transactional
    public void verifyLoginChallenge(final String challengeId, final Integer code) {
        final var now = LocalDateTime.now();

        final var challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid challenge"));

        if (challenge.isVerified() || challenge.isExpired(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Challenge expired");
        }

        if (challenge.getAttempts() >= 5) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many attempts");
        }

        final var userId = challenge.getUserId();
        final var mfa = userMfaRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "MFA not enabled"));

        if (!Boolean.TRUE.equals(mfa.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "MFA not enabled");
        }

        final var secret = crypto.decrypt(mfa.getSecret());

        final var ok = totpService.verify(secret, code);
        challenge.setAttempts(challenge.getAttempts() + 1);

        if (!ok) {
            challengeRepository.save(challenge);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP");
        }

        challenge.setVerifiedAt(now);
        challengeRepository.save(challenge);
    }

    private String mask(final String secret) {
        if (secret.length() <= 4) return "****";
        return "****" + secret.substring(secret.length() - 4);
    }

    public Optional<MfaChallenge> findById(@NotBlank String id) {
        return challengeRepository.findById(id);
    }
}
