package hardcoders808.bata.bank.backend.jpa.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

import hardcoders808.bata.bank.backend.enums.CardStatus;
import hardcoders808.bata.bank.backend.enums.CardType;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "card_number", unique = true, nullable = false, length = 19)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, length = 4)
    private String cvv;

    @Column(name = "pin_hash", nullable = false)
    private String pinHash; // BCrypt hashed PIN

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 20)
    private CardType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @Column(name = "daily_limit", precision = 19, scale = 4)
    private BigDecimal dailyLimit;

    @Column(name = "monthly_limit", precision = 19, scale = 4)
    private BigDecimal monthlyLimit;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("cardHolderName", cardHolderName)
                .append("cardNumber", "****-****-****-" + (cardNumber != null && cardNumber.length() > 4 ?
                        cardNumber.substring(cardNumber.length() - 4) : "####"))
                .append("expiryDate", expiryDate)
                .append("type", type)
                .append("status", status)
                .append("dailyLimit", dailyLimit)
                .append("monthlyLimit", monthlyLimit)
                .append("createdAt", createdAt)
                .toString();
    }
}