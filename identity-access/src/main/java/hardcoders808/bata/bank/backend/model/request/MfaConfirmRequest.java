package hardcoders808.bata.bank.backend.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:26
 */
public record MfaConfirmRequest(
        @NotBlank(message = "OTP code is required")
        @Size(min = 6, max = 6, message = "OTP code must be 6 digits")
        Integer code) {
}