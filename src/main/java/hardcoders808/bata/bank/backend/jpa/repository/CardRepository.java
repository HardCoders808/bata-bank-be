package hardcoders808.bata.bank.backend.jpa.repository;

import hardcoders808.bata.bank.backend.jpa.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

}
