package hardcoders808.bata.bank.backend.jpa.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:14
 */

@Entity
@Getter
@Setter
@Builder
@ToString
@Table(name = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "device_id")
    private String deviceId;
    @Column(name = "token_hash")
    private String tokenHash;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    @Column(name = "replaced_by_hash")
    private String replacedByHash;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static RefreshToken of(final String userId, final String deviceId, final String refreshHash, final LocalDateTime expiresAt) {
        return new RefreshToken(UUID.randomUUID().toString(), userId, deviceId, refreshHash, expiresAt, null, null, LocalDateTime.now());
    }
}
