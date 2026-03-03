package hardcoders808.bata.bank.backend.resource;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/register")
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
}
