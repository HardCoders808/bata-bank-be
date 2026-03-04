package hardcoders808.bata.bank.backend.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:02
 */
public record MfaVerifyRequest(
        @NotBlank String challengeId,
        @NotNull  Integer code
) { }