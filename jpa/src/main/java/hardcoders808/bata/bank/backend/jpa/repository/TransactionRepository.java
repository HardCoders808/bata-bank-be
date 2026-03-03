package hardcoders808.bata.bank.backend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hardcoders808.bata.bank.backend.jpa.domain.Transaction;
import hardcoders808.bata.bank.backend.jpa.domain.TransactionPk;

public interface TransactionRepository extends JpaRepository<Transaction, TransactionPk> {

}
