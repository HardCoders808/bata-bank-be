package hardcoders808.bata.bank.backend.accounts.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import hardcoders808.bata.bank.backend.accounts.model.request.AccountCreateRequestDTO;
import hardcoders808.bata.bank.backend.accounts.model.response.AccountResponseDTO;
import hardcoders808.bata.bank.backend.accounts.service.AccountService;
import hardcoders808.bata.bank.backend.jpa.domain.User;
import hardcoders808.bata.bank.backend.jpa.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${endpoint.api.account-resource}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AccountResource {

    private final AccountService accountService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts(@AuthenticationPrincipal Jwt jwt) {
        return findUser(jwt)
                .map(user -> ResponseEntity.ok(accountService.getAccountsByUser(user)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/create")
    public ResponseEntity<AccountResponseDTO> createAccount(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AccountCreateRequestDTO request) {
        
        return findUser(jwt)
                .map(user -> {
                    AccountResponseDTO response = accountService.createAccount(user, request);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> getAccountDetails(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long accountId) {
        
        return findUser(jwt)
                .flatMap(user -> accountService.getAccountDetails(accountId, user))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private java.util.Optional<User> findUser(Jwt jwt) {
        return userRepository.findByEmail(jwt.getSubject());
    }
}
