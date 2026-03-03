package hardcoders808.bata.bank.backend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hardcoders808.bata.bank.backend.jpa.domain.UserMfa;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:01
 */

public interface UserMfaRepository extends JpaRepository<UserMfa, Long> {
}