package hardcoders808.bata.bank.backend.resource;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import hardcoders808.bata.bank.backend.service.TransactionService;

@Resource
@RequestMapping("/transactions")
public class TransactionResource {

    private static final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    private final TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

}
