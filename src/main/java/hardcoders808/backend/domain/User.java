package hardcoders808.backend.domain;

import hardcoders808.backend.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(nullable = false, length = 20)
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("email", email)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("role", role)
                .append("accountGroup", accountGroup)
                .append("dateOfBirth", dateOfBirth)
                .append("createdAt", createdAt)
                .toString();
    }
}
