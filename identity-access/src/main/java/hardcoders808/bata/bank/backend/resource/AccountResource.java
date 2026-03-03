package hardcoders808.bata.bank.backend.resource;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import hardcoders808.bata.bank.backend.service.AccountService;

@Resource
@RequestMapping("/accounts")
public class AccountResource {

    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

}
