package hardcoders808.bata.bank.backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import lombok.experimental.UtilityClass;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:29
 */
@UtilityClass
public class KeyUtils {

    public static RSAPublicKey loadRsaPublicKeyFromPem(final String pem) {
        try {
            final var der = decodePem(pem,
                    "-----BEGIN PUBLIC KEY-----",
                    "-----END PUBLIC KEY-----");

            final var spec = new X509EncodedKeySpec(der);
            final var kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to load RSA public key from PEM", e);
        }
    }

    public static RSAPrivateKey loadRsaPrivateKeyFromPem(final String pem) {
        try {
            final var der = decodePem(pem,
                    "-----BEGIN PRIVATE KEY-----",
                    "-----END PRIVATE KEY-----");

            final var spec = new PKCS8EncodedKeySpec(der);
            final var kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to load RSA private key from PEM", e);
        }
    }

    private static byte[] decodePem(final String pem,
                                    final String beginMarker,
                                    final String endMarker) {

        final var base64 = pem
                .replace(beginMarker, "")
                .replace(endMarker, "")
                .replaceAll("\\s", "");

        return Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
    }
}
