package hardcoders808.bata.bank.backend.cards.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hardcoders808.bata.bank.backend.cards.model.request.CardCreateRequestDTO;
import hardcoders808.bata.bank.backend.cards.model.response.CardResponseDTO;
import hardcoders808.bata.bank.backend.enums.CardStatus;
import hardcoders808.bata.bank.backend.jpa.domain.Account;
import hardcoders808.bata.bank.backend.jpa.domain.Card;
import hardcoders808.bata.bank.backend.jpa.domain.User;
import hardcoders808.bata.bank.backend.jpa.repository.AccountRepository;
import hardcoders808.bata.bank.backend.jpa.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<CardResponseDTO> getAllCards(Pageable pageable) {
        log.debug("Fetching page {} of all cards", pageable.getPageNumber());
        return cardRepository.findAll(pageable)
                .map(CardResponseDTO::fromDomain);
    }

    public List<CardResponseDTO> getCardsByUser(User user) {
        return cardRepository.findAllByAccount_Owner(user).stream()
                .map(CardResponseDTO::fromDomain)
                .toList();
    }

    public List<CardResponseDTO> getCardsByAccount(User user, Long accountId) {
        return cardRepository.findAllByAccount_IdAndAccount_Owner(accountId, user).stream()
                .map(CardResponseDTO::fromDomain)
                .toList();
    }

    @Transactional
    public CardResponseDTO issueCard(User user, CardCreateRequestDTO request) {
        Account account = accountRepository.findById(request.accountId())
                .filter(acc -> acc.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Account not found or not owned by user"));

        String cardNumber = generateCardNumber();
        String cvv = generateCvv();
        String pinHash = passwordEncoder.encode(request.pin());

        Card card = Card.builder()
                .account(account)
                .cardNumber(cardNumber)
                .cardHolderName(request.cardHolderName())
                .expiryDate(LocalDate.now().plusYears(3))
                .cvv(cvv)
                .pinHash(pinHash)
                .type(request.cardType())
                .status(CardStatus.ACTIVE)
                .dailyLimit(new BigDecimal("1000.0000"))   // Default daily limit
                .monthlyLimit(new BigDecimal("5000.0000")) // Default monthly limit
                .build();

        Card saved = cardRepository.save(card);
        log.info("Issued new {} card for account {}: {}", request.cardType(), account.getAccountNumber(), cardNumber);
        return CardResponseDTO.fromDomain(saved);
    }

    public Optional<CardResponseDTO> getCardDetails(Long cardId, User user) {
        return cardRepository.findById(cardId)
                .filter(card -> card.getAccount().getOwner().getId().equals(user.getId()))
                .map(CardResponseDTO::fromDomain);
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("4242"); // Mock BIN
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}
