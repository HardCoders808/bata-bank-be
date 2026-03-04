package hardcoders808.bata.bank.backend.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hardcoders808.bata.bank.backend.jpa.domain.Card;

import java.util.List;
import hardcoders808.bata.bank.backend.jpa.domain.User;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByAccount_Owner(User owner);
    List<Card> findAllByAccount_IdAndAccount_Owner(Long accountId, User owner);
}
