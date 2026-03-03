package hardcoders808.bata.bank.backend.model.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import hardcoders808.bata.bank.backend.enums.UserRole;

public record UserRegistrationRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Size(min = 8) String password,
        @NotBlank UserRole userRole,
        @NotBlank LocalDate dateOfBirth,
        @NotBlank String idNumber,
        @NotBlank String birthNumber,
        @NotBlank String address) {
}
