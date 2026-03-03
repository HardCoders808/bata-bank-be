package hardcoders808.bata.bank.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import hardcoders808.bata.bank.backend.jpa.repository.AccountRepository;

@Service
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

}
