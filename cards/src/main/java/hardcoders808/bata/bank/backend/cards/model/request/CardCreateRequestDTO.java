package hardcoders808.bata.bank.backend.cards.model.request;

import hardcoders808.bata.bank.backend.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CardCreateRequestDTO(
        @NotNull Long accountId,
        @NotNull CardType cardType,
        @NotBlank String cardHolderName,
        @NotBlank @Size(min = 4, max = 4) String pin
) {
}
