package hardcoders808.bata.bank.backend.jpa.repository;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hardcoders808.bata.bank.backend.jpa.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final @NotNull String email);

    boolean existsByEmail(final @NotNull String email);
}
