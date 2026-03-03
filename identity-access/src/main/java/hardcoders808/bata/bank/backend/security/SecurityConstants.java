package hardcoders808.bata.bank.backend.security;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 14:34
 */
public class SecurityConstants {
    public final static long REFRESH_TTL_DAYS = 14L;

    public final static long TOKEN_VALID_FOR_SECONDS = 1 * 15L;

    public final static String REFRESH_TOKEN = "refresh_token";

    public final static String DEVICE_ID = "device_id";
}
