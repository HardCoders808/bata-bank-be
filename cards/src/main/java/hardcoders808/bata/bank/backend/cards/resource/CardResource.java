package hardcoders808.bata.bank.backend.cards.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import hardcoders808.bata.bank.backend.cards.model.request.CardCreateRequestDTO;
import hardcoders808.bata.bank.backend.cards.model.response.CardResponseDTO;
import hardcoders808.bata.bank.backend.cards.service.CardService;
import hardcoders808.bata.bank.backend.jpa.domain.User;
import hardcoders808.bata.bank.backend.jpa.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${endpoint.api.card-resource}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CardResource {

    private final CardService cardService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<List<CardResponseDTO>> getMyCards(@AuthenticationPrincipal Jwt jwt) {
        return findUser(jwt)
                .map(user -> ResponseEntity.ok(cardService.getCardsByUser(user)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardResponseDTO>> getCardsByAccount(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long accountId) {
        
        return findUser(jwt)
                .map(user -> ResponseEntity.ok(cardService.getCardsByAccount(user, accountId)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/create")
    public ResponseEntity<CardResponseDTO> issueCard(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CardCreateRequestDTO request) {
        
        return findUser(jwt)
                .map(user -> {
                    try {
                        CardResponseDTO response = cardService.issueCard(user, request);
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    } catch (IllegalArgumentException e) {
                        log.warn("Card issuance failed: {}", e.getMessage());
                        return ResponseEntity.badRequest().<CardResponseDTO>build();
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> getCardDetails(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cardId) {
        
        return findUser(jwt)
                .flatMap(user -> cardService.getCardDetails(cardId, user))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private java.util.Optional<User> findUser(Jwt jwt) {
        return userRepository.findByEmail(jwt.getSubject());
    }
}
