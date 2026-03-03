package hardcoders808.bata.bank.backend.jpa.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.NullMarked;

import lombok.*;

import hardcoders808.bata.bank.backend.enums.UserRole;

@Entity
@Getter
@Setter
@Builder
@ToString
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Column(name = "account_group", nullable = false, length = 50)
    private String accountGroup;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "id_number", nullable = false, unique = true, length = 20)
    private String idNumber;

    @Column(name = "birth_number", nullable = false, unique = true, length = 20)
    private String birthNumber;

    @Column(nullable = false, length = 255)
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserMfa userMfa;

    public Boolean isMfaEnabled() {
        if (Objects.isNull(userMfa)) {
            return false;
        }
        return Boolean.TRUE.equals(userMfa.getEnabled());
    }


    @NullMarked
    public String getUsername() {
        return email;
    }
}
