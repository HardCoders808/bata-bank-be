package hardcoders808.bata.bank.backend.jpa.repository;

import java.util.Optional;

import hardcoders808.bata.bank.backend.enums.UserRole;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hardcoders808.bata.bank.backend.jpa.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final @NotNull String email);

    boolean existsByEmail(final @NotNull String email);

    boolean existsByEmailAndAccountGroup(final @NotNull String parentEmail, final @NotNull String userGroup);

    Page<User> findAll(final @NotNull Pageable pageable);

    Page<User> findAllByRoleNot(final @NotNull UserRole role, final @NotNull Pageable pageable);
}
