package hardcoders808.bata.bank.backend.accounts.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import hardcoders808.bata.bank.backend.enums.AccountStatus;
import hardcoders808.bata.bank.backend.enums.AccountType;
import hardcoders808.bata.bank.backend.jpa.domain.Account;
import lombok.Builder;

@Builder
public record AccountResponseDTO(
        Long id,
        String accountNumber,
        String iban,
        AccountType accountType,
        String currency,
        BigDecimal balance,
        AccountStatus status,
        LocalDateTime createdAt
) {
    public static AccountResponseDTO fromDomain(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .iban(account.getIban())
                .accountType(account.getAccountType())
                .currency(account.getCurrencyType())
                .balance(account.getBalance().getNumber().numberValue(BigDecimal.class))
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
