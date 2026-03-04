package hardcoders808.bata.bank.backend.accounts.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hardcoders808.bata.bank.backend.accounts.model.request.AccountCreateRequestDTO;
import hardcoders808.bata.bank.backend.accounts.model.response.AccountResponseDTO;
import hardcoders808.bata.bank.backend.enums.AccountStatus;
import hardcoders808.bata.bank.backend.jpa.domain.Account;
import hardcoders808.bata.bank.backend.jpa.domain.User;
import hardcoders808.bata.bank.backend.jpa.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountResponseDTO> getAccountsByUser(User user) {

        return accountRepository.findAllByOwner(user).stream()
                .map(AccountResponseDTO::fromDomain)
                .toList();
    }

    @Transactional
    public AccountResponseDTO createAccount(User owner, AccountCreateRequestDTO request) {
        String accountNumber = generateAccountNumber();
        String iban = generateIban(accountNumber);

        Account account = Account.builder()
                .owner(owner)
                .accountNumber(accountNumber)
                .iban(iban)
                .accountType(request.accountType())
                .currencyType(request.currency())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .build();

        Account saved = accountRepository.save(account);
        log.info("Created new {} account for user {}: {}", request.accountType(), owner.getEmail(), accountNumber);
        return AccountResponseDTO.fromDomain(saved);
    }

    public Optional<AccountResponseDTO> getAccountDetails(Long accountId, User user) {
        return accountRepository.findById(accountId)
                .filter(acc -> acc.getOwner().getId().equals(user.getId()))
                .map(AccountResponseDTO::fromDomain);
    }

    private String generateAccountNumber() {
        return String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())).substring(0, 10);
    }

    private String generateIban(String accountNumber) {
        return "CZ" + "00" + "0800" + "000000" + accountNumber;
    }
}
