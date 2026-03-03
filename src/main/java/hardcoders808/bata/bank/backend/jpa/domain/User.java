package hardcoders808.bata.bank.backend.jpa.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

import hardcoders808.bata.bank.backend.enums.UserRole;
import hardcoders808.bata.bank.backend.model.request.UserRegistrationRequestDTO;
import hardcoders808.bata.bank.backend.model.response.UserRegistrationResponseDTO;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

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
    @NullMarked
    public Collection<GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    @NullMarked
    public String getUsername() {
        return email;
    }


    public UserRegistrationResponseDTO asDto() {
        return new UserRegistrationResponseDTO(
                email,
                firstName,
                lastName,
                role,
                dateOfBirth,
                idNumber,
                birthNumber,
                address
        );
    }

    public static User fromUserRegistrationRequestDTO(final UserRegistrationRequestDTO request, final String encodedPassword) {
        return User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(encodedPassword)
                .role(request.userRole())
                .dateOfBirth(request.dateOfBirth())
                .idNumber(request.idNumber())
                .birthNumber(request.birthNumber())
                .address(request.address())
                .build();
    }
}
