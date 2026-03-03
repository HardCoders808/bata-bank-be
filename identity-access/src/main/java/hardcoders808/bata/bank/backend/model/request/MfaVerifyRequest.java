package hardcoders808.bata.bank.backend.model.request;

import jakarta.validation.constraints.NotBlank;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:02
 */
public record MfaVerifyRequest(
        @NotBlank String challengeId,
        @NotBlank Integer code
) {
}