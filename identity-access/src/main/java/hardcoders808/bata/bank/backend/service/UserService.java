package hardcoders808.bata.bank.backend.service;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import hardcoders808.bata.bank.backend.jpa.domain.User;
import hardcoders808.bata.bank.backend.jpa.repository.UserRepository;
import hardcoders808.bata.bank.backend.model.request.UserRegistrationRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public Optional<User> registerUser(final @NotNull UserRegistrationRequestDTO request) {
        final var rawPassword = request.password();
        final var encodedPassword = passwordEncoder.encode(rawPassword);

        final var existsByEmail = userRepository.existsByEmail(request.email());
        if (existsByEmail) {
            log.info("user with email [{}] already exists, terminating registering user", request.email());
            return Optional.empty();
        }


        final var user = request.toDomain(encodedPassword);
        log.info("User Created: {}", user);
        return Optional.of(userRepository.save(user));
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(UserService::convertUser)
                .orElseThrow(() -> new UsernameNotFoundException("user with identification " + username + " does not exist"));
    }

    private static org.springframework.security.core.userdetails.@NonNull User convertUser(User user) {
        final var authority = new SimpleGrantedAuthority(user.getRole().name());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
