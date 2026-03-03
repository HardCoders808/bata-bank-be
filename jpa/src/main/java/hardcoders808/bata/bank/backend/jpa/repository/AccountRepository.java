package hardcoders808.bata.bank.backend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hardcoders808.bata.bank.backend.jpa.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}

