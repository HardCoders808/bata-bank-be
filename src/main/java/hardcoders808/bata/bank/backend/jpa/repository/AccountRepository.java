package hardcoders808.bata.bank.backend.jpa.repository;

import hardcoders808.bata.bank.backend.jpa.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}

