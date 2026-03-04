package hardcoders808.bata.bank.backend.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

import hardcoders808.bata.bank.backend.enums.TransactionStatus;
import hardcoders808.bata.bank.backend.jpa.domain.Transaction;

@Builder
public record TransactionDisplayDTO(
    String id,
    Long sourceAccountId,
    Long targetAccountId,
    BigDecimal amount,
    String currency,
    TransactionStatus status,
    String description,
    LocalDateTime createdAt
) {
    public static TransactionDisplayDTO fromDomain(Transaction transaction) {
        return TransactionDisplayDTO.builder()
                .id(transaction.getId().getTrxUuid())
                .sourceAccountId(transaction.getSourceAccount() != null ? transaction.getSourceAccount().getId() : null)
                .targetAccountId(transaction.getTargetAccount() != null ? transaction.getTargetAccount().getId() : null)
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getId().getTerminalDttm())
                .build();
    }
}
