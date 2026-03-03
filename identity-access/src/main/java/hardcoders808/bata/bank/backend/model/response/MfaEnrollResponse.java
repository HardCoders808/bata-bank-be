package hardcoders808.bata.bank.backend.model.response;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:02
 */
public record MfaEnrollResponse(
        String otpAuthUrl,
        String secretMasked
) {}