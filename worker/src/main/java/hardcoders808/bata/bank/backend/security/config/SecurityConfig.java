package hardcoders808.bata.bank.backend.security.config;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import hardcoders808.bata.bank.backend.security.SecurityConstants;
import hardcoders808.bata.bank.backend.security.exception.UnauthorizedEntryPoint;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig {
    private final UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http, final JwtAuthenticationConverter converter) {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // Disable for stateless REST APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v1/error",
                                "/error",
                                "/api/v1/users/authenticate",
                                "/api/v1/auth/authenticate",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/public/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated() // Protect everything else
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Best for Next.js frontend
                )
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                )
                .oauth2ResourceServer(oAuth2 ->
                        oAuth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final var cfg = new CorsConfiguration();

        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://10.0.2.2:*",
                "http://127.0.0.1:*"
        ));

        cfg.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));

        cfg.setAllowedHeaders(List.of(
                "content-type",
                "authorization",
                "X-XSRF-TOKEN",
                SecurityConstants.REFRESH_TOKEN,
                SecurityConstants.DEVICE_ID
        ));

        cfg.setExposedHeaders(List.of("set-cookie"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(final PasswordEncoder passwordEncoder) {
        final var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final var converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter((Jwt jwt) -> {
            Object rolesClaim = jwt.getClaims().get("roles");
            if (!(rolesClaim instanceof List<?> rolesList)) {
                return Collections.emptyList();
            }
            return rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return converter;
    }
}
