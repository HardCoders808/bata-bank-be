package hardcoders808.bata.bank.backend.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:10
 */
public record LoginRequest(@NotBlank @Email String email, @NotBlank @Size(min = 8) String password) {
}
