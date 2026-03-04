package hardcoders808.bata.bank.backend.resource;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import hardcoders808.bata.bank.backend.model.request.CreateTransactionRequestDTO;
import hardcoders808.bata.bank.backend.model.response.TransactionDisplayDTO;
import hardcoders808.bata.bank.backend.service.TransactionService;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionResource {

    private static final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<TransactionDisplayDTO> createTransaction(@Valid @RequestBody CreateTransactionRequestDTO request) {
        log.info("Received transaction request: {}", request);
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<TransactionDisplayDTO>> getAllTransactions(Pageable pageable) {
        log.info("Fetching all transactions");
        return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
    }
}
