package hardcoders808.bata.bank.backend.resource;

import hardcoders808.bata.bank.backend.enums.UserRole;
import hardcoders808.bata.bank.backend.model.request.JuniorUserRegistrationRequestDTO;
import hardcoders808.bata.bank.backend.model.request.UpdateUserDTO;
import hardcoders808.bata.bank.backend.model.response.UserDisplayDTO;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import hardcoders808.bata.bank.backend.model.request.UserRegistrationRequestDTO;
import hardcoders808.bata.bank.backend.model.response.UserRegistrationResponseDTO;
import hardcoders808.bata.bank.backend.service.UserService;

@Slf4j
@RestController
@RequestMapping("${endpoint.api.user-resource}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserResource {

    private final UserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'BANKER')")
    public ResponseEntity<Page<UserDisplayDTO>> getUsers(Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        final UserRole role = extractRole(jwt);
        log.info("JWT Subject: {} with role: {} is fetching users", jwt.getSubject(), role);
        return ResponseEntity.ok(userService.getUsersForRole(role, pageable));
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'BANKER')")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserDTO request) {

        log.info("Update attempt for User ID: [{}]", userId);

        boolean isSuccess = userService.updateUser(userId, request);

        if (isSuccess) {
            log.info("User successfully updated: {}", request.email());
            return ResponseEntity.ok().build();
        } else {
            log.warn("Update failed: User ID [{}] not found", userId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDisplayDTO> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        log.info("Fetching profile for user: {}", email);

        return userService.findByEmail(email)
                .map(UserDisplayDTO::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'BANKER')")
    public ResponseEntity<UserRegistrationResponseDTO> registerUser(final @Valid @RequestBody UserRegistrationRequestDTO request) {
        log.info("Registration attempt for email: {}", request.email());
        return userService.registerUser(request)
                .map(user -> {
                    log.info("User successfully registered: {}", request.email());
                    final var response = UserRegistrationResponseDTO.fromDomain(user);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/register-junior")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'BANKER')")
    public ResponseEntity<UserRegistrationResponseDTO> registerJuniorAccountHolder(final @Valid @RequestBody JuniorUserRegistrationRequestDTO request) {
        log.info("Junior Account Holder registration attempt for email: {}", request.email());
        return userService.registerJuniorAccount(request)
                .map(user -> {
                    log.info("Junior Account Holder successfully registered: {}", request.email());
                    final var response = UserRegistrationResponseDTO.fromDomain(user);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private UserRole extractRole(Jwt jwt) {
        return jwt.getClaimAsStringList("roles").stream()
                .map(UserRole::fromString)
                .findFirst()
                .orElse(UserRole.UNKNOWN);
    }
}
