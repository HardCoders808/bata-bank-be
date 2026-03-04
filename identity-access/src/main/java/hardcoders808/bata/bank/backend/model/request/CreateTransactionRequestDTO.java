package hardcoders808.bata.bank.backend.model.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTransactionRequestDTO(
    @NotNull(message = "Source account ID is required")
    Long sourceAccountId,

    @NotNull(message = "Target account ID is required")
    Long targetAccountId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    String currency,

    @Size(max = 255, message = "Description must be less than 255 characters")
    String description
) {}
