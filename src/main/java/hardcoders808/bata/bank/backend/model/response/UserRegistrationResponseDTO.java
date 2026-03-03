package hardcoders808.bata.bank.backend.model.response;

import java.time.LocalDate;

import hardcoders808.bata.bank.backend.enums.UserRole;

public record UserRegistrationResponseDTO(

        String email,

        String firstName,

        String lastName,

        UserRole userRole,

        LocalDate dateOfBirth,

        String idNumber,

        String birthNumber,

        String address
) {
}
