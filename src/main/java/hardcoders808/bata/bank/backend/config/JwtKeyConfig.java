package hardcoders808.bata.bank.backend.config;

import static hardcoders808.bata.bank.backend.utils.KeyUtils.loadRsaPrivateKeyFromPem;
import static hardcoders808.bata.bank.backend.utils.KeyUtils.loadRsaPublicKeyFromPem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:26
 */
@Configuration
public class JwtKeyConfig {
    @Bean
    public JwtEncoder jwtEncoder(final @Value("${security.jwt.public-key-pem}") String publicKeyPem,
                                 final @Value("${security.jwt.private-key-pem}") String privateKeyPem) {

        final var publicKey = loadRsaPublicKeyFromPem(publicKeyPem);
        final var privateKey = loadRsaPrivateKeyFromPem(privateKeyPem);

        final var rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("bata-bank-key-1")
                .build();

        return new NimbusJwtEncoder((selector, context) -> selector.select(new JWKSet(rsaKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder(final @Value("${security.jwt.public-key-pem}") String publicKeyPem) {
        final var publicKey = loadRsaPublicKeyFromPem(publicKeyPem);
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

}
