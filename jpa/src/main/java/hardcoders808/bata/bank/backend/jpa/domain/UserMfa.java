package hardcoders808.bata.bank.backend.jpa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 20:11
 */

@Entity
@Getter
@Setter
@Builder
@ToString
@Table(name = "user_mfa")
@NoArgsConstructor
@AllArgsConstructor
public class UserMfa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @MapsId
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private String secret;
    private Boolean enabled;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
