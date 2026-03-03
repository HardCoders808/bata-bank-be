package hardcoders808.bata.bank.backend.security.mfa;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.GoogleAuthenticator;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:06
 */
@Component
public final class TotpService {
    private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

    public String generateSecret() {
        return authenticator.createCredentials().getKey();
    }

    public boolean verify(final String secret, final Integer code) {
        return authenticator.authorize(secret, code);
    }

    public String buildOtpAuthUrl(final String issuer, final String accountName, final String secret) {
        final var label = issuer + ":" + accountName;
        return "otpauth://totp/" + urlEncode(label)
                + "?secret=" + urlEncode(secret)
                + "&issuer=" + urlEncode(issuer);
    }

    private String urlEncode(final String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
