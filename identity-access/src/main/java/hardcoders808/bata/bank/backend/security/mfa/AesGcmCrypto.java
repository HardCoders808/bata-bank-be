package hardcoders808.bata.bank.backend.security.mfa;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:04
 */
@Component
public class AesGcmCrypto {
    private final byte[] keyBytes;
    private final SecureRandom random;

    public AesGcmCrypto(final @Value("${security.crypto.mfa-key-base64}") String base64Key) {
        this.keyBytes = Base64.getDecoder().decode(base64Key);
        this.random = new SecureRandom();
    }

    public String encrypt(final String plaintext) {
        try {
            final var iv = new byte[12];
            random.nextBytes(iv);

            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final var key = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));

            final var ct = cipher.doFinal(plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            final var out = new byte[iv.length + ct.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ct, 0, out, iv.length, ct.length);

            return Base64.getEncoder().encodeToString(out);
        } catch (final Exception e) {
            throw new IllegalStateException("Secret encryption failed", e);
        }
    }

    public String decrypt(final String base64Ciphertext) {
        try {
            final var in = Base64.getDecoder().decode(base64Ciphertext);

            final var iv = new byte[12];
            System.arraycopy(in, 0, iv, 0, 12);

            final var ct = new byte[in.length - 12];
            System.arraycopy(in, 12, ct, 0, ct.length);

            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final var key = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));

            final var pt = cipher.doFinal(ct);
            return new String(pt, java.nio.charset.StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new IllegalStateException("Secret decryption failed", e);
        }
    }
}
