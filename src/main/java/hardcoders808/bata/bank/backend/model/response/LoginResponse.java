package hardcoders808.bata.bank.backend.model.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:11
 */
public record LoginResponse(@NotBlank @NotNull String token, long expiresIn) {
}
