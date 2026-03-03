package hardcoders808.bata.bank.backend.jpa.repository;

import hardcoders808.bata.bank.backend.jpa.domain.Transaction;
import hardcoders808.bata.bank.backend.jpa.domain.TransactionPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, TransactionPk> {

}
