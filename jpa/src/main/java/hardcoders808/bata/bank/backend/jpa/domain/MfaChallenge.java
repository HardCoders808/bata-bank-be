package hardcoders808.bata.bank.backend.jpa.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:01
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@Table(name = "mfa_challenge")
@NoArgsConstructor
@AllArgsConstructor
public class MfaChallenge {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String purpose; // "LOGIN"

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Integer attempts;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public boolean isExpired(final LocalDateTime now) {
        return expiresAt.isBefore(now);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }
}