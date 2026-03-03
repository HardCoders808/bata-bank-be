package hardcoders808.bata.bank.backend.jpa.repository;


import hardcoders808.bata.bank.backend.jpa.domain.MfaChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:02
 */
public interface MfaChallengeRepository extends JpaRepository<MfaChallenge, String> {
}