package hardcoders808.bata.bank.backend.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hardcoders808.bata.bank.backend.jpa.domain.RefreshToken;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:14
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenHash(String presentedHash);
}
