package hardcoders808.bata.bank.backend.accounts.model.request;

import hardcoders808.bata.bank.backend.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountCreateRequestDTO(
        @NotNull AccountType accountType,
        @NotBlank String currency
) {
}
