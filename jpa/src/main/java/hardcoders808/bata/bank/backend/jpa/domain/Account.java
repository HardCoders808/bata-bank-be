package hardcoders808.bata.bank.backend.jpa.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.money.MonetaryAmount;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javamoney.moneta.Money;

import lombok.*;

import hardcoders808.bata.bank.backend.enums.AccountStatus;
import hardcoders808.bata.bank.backend.enums.AccountType;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(unique = true, nullable = false)
    private String iban;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "currency_type", length = 3, nullable = false)
    private String currencyType;

    @Column(precision = 19, scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public MonetaryAmount getBalance() {
        return Money.of(balance, currencyType);
    }
}