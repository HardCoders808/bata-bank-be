package hardcoders808.bata.bank.backend.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;

import lombok.experimental.UtilityClass;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:29
 */
@UtilityClass
public class KeyUtils {
    public static byte[] readPem(final Resource resource) {
        try (final var reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            final var text = reader.lines().collect(Collectors.joining("\n"));

            final var base64 = text
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            return Base64.getDecoder().decode(base64);

        } catch (final Exception e) {
            throw new IllegalStateException("Failed to read PEM file", e);
        }
    }

    public static RSAPrivateKey loadRsaPrivateKey(final Resource resource) {
        try {
            final var keySpec = new PKCS8EncodedKeySpec(readPem(resource));
            final var keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        } catch (final Exception e) {
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
    }

    public static RSAPublicKey loadRsaPublicKey(final Resource resource) {
        try {
            final var keySpec = new X509EncodedKeySpec(readPem(resource));
            final var keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPublicKey) keyFactory.generatePublic(keySpec);

        } catch (final Exception e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }
}
