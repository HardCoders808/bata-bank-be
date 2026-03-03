package hardcoders808.bata.bank.backend.config;

import static hardcoders808.bata.bank.backend.utils.KeyUtils.loadRsaPrivateKey;
import static hardcoders808.bata.bank.backend.utils.KeyUtils.loadRsaPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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
    public JwtEncoder jwtEncoder() {
        final var publicKey = loadRsaPublicKey(new ClassPathResource("keystore/public_key.pem"));

        final var privateKey = loadRsaPrivateKey(new ClassPathResource("keystore/private_key.pem"));

        final var rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("bata-bank-key-1")
                .build();

        return new NimbusJwtEncoder((selector, context) ->
                selector.select(new JWKSet(rsaKey))
        );
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        final var publicKey = loadRsaPublicKey(new ClassPathResource("keystore/public_key.pem"));
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

}
