package hardcoders808.bata.bank.backend.service;

import java.util.List;
import java.util.Optional;

import hardcoders808.bata.bank.backend.enums.UserRole;
import hardcoders808.bata.bank.backend.model.request.UpdateUserDTO;
import hardcoders808.bata.bank.backend.model.response.UserDisplayDTO;
import jakarta.transaction.Transactional;
import hardcoders808.bata.bank.backend.model.request.JuniorUserRegistrationRequestDTO;
import jakarta.validation.constraints.NotNull;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MfaService mfaService;

    public Page<UserDisplayDTO> getUsersForRole(UserRole currentUserRole, Pageable pageable) {
        Page<User> users;

        if (currentUserRole.isAdmin()) {
            users = userRepository.findAll(pageable);
        } else if (currentUserRole.isBanker()) {
            users = userRepository.findAllByRoleNot(UserRole.SYS_ADMIN, pageable);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view the user list");
        }

        return users.map(UserDisplayDTO::fromDomain);
    }

    public Optional<User> registerJuniorAccount(final @NotNull JuniorUserRegistrationRequestDTO request) {
        final var rawPassword = request.password();
        final var encodedPassword = passwordEncoder.encode(rawPassword);

        final var parentExists = userRepository.existsByEmailAndAccountGroup(request.parentEmail(), request.accountGroup());
        if (!parentExists) {
            log.info("parent account with email [{}]doesnt exist, terminating registering user", request.email());
            return Optional.empty();
        }

        final var existsByEmail = userRepository.existsByEmail(request.email());
        if (existsByEmail) {
            log.info("user with email [{}] already exists, terminating registering user", request.email());
            return Optional.empty();
        }

        final var user = request.toDomain(encodedPassword);
        log.info("Junior account successfully registered. Email: [{}], Linked to Parent: [{}]", user.getEmail(), request.parentEmail());
        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public boolean updateUser(final @NotNull Long userId, final @NotNull UpdateUserDTO request) {
        return userRepository.findById(userId)
                .map(user -> {
                    request.updateEntity(user);
                    this.userRepository.save(user);

                    log.info("User with ID [{}] and email [{}] successfully updated", userId, user.getEmail());
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Update failed: User with ID [{}] not found", userId);
                    return false;
                });
    }

    @Transactional
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
        final var saved = userRepository.save(user);
        return Optional.of(saved);
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

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(final Long userId) {
        return userRepository.findById(userId);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
