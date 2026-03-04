package hardcoders808.bata.bank.backend.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hardcoders808.bata.bank.backend.enums.TransactionStatus;
import hardcoders808.bata.bank.backend.enums.TransactionType;
import hardcoders808.bata.bank.backend.jpa.domain.Account;
import hardcoders808.bata.bank.backend.jpa.domain.Transaction;
import hardcoders808.bata.bank.backend.jpa.domain.TransactionPk;
import hardcoders808.bata.bank.backend.jpa.repository.AccountRepository;
import hardcoders808.bata.bank.backend.jpa.repository.TransactionRepository;
import hardcoders808.bata.bank.backend.model.request.CreateTransactionRequestDTO;
import hardcoders808.bata.bank.backend.model.response.TransactionDisplayDTO;

@Service
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransactionDisplayDTO createTransaction(CreateTransactionRequestDTO request) {
        log.info("Creating transaction: {}", request);

        // Validate source account
        Account sourceAccount = accountRepository.findById(request.sourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        // Validate target account
        Account targetAccount = accountRepository.findById(request.targetAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));

        // Validate currency (should match accounts?)
        if (!sourceAccount.getCurrencyType().equals(request.currency())) {
            throw new IllegalArgumentException("Currency mismatch with source account");
        }
        if (!targetAccount.getCurrencyType().equals(request.currency())) {
            throw new IllegalArgumentException("Currency mismatch with target account");
        }

        // Validate balance
        MonetaryAmount balance = sourceAccount.getBalance();
        MonetaryAmount transferAmount = Money.of(request.amount(), request.currency());
        
        if (balance.compareTo(transferAmount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Create transaction
        Transaction transaction = Transaction.builder()
                .id(new TransactionPk(LocalDateTime.now(), UUID.randomUUID().toString()))
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .amount(request.amount())
                .currency(request.currency())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING) 
                .description(request.description())
                .build();

        // Save transaction
        transaction = transactionRepository.save(transaction);

        // Update balances
        MonetaryAmount newSourceBalance = balance.subtract(transferAmount);
        MonetaryAmount newTargetBalance = targetAccount.getBalance().add(transferAmount);
        
        sourceAccount.setBalance(newSourceBalance.getNumber().numberValue(BigDecimal.class));
        targetAccount.setBalance(newTargetBalance.getNumber().numberValue(BigDecimal.class));
        
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction = transactionRepository.save(transaction);

        log.info("Transaction created successfully: {}", transaction.getId().getTrxUuid());
        return TransactionDisplayDTO.fromDomain(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionDisplayDTO> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(TransactionDisplayDTO::fromDomain);
    }
}
