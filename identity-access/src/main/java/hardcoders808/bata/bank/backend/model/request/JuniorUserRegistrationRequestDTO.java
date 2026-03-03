package hardcoders808.bata.bank.backend.model.request;

import hardcoders808.bata.bank.backend.enums.UserRole;
import hardcoders808.bata.bank.backend.jpa.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record JuniorUserRegistrationRequestDTO(
        @NotBlank @Email String parentEmail,
        @NotBlank String accountGroup,

        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Size(min = 8) String password,
        @NotNull UserRole userRole,
        @NotNull LocalDate dateOfBirth,
        @NotBlank String idNumber,
        @NotBlank String birthNumber,
        @NotBlank String address) {

    public User toDomain(final String encodedPassword) {
        return User.builder()
                .email(email())
                .firstName(firstName())
                .lastName(lastName())
                .password(encodedPassword)
                .role(userRole())
                .accountGroup(accountGroup())
                .dateOfBirth(dateOfBirth())
                .idNumber(idNumber())
                .birthNumber(birthNumber())
                .address(address())
                .build();
    }
}

