package hardcoders808.bata.bank.backend.model.response;

import hardcoders808.bata.bank.backend.enums.UserRole;

import java.time.LocalDate;

public record UserRegistrationResponseDTO(

        String email,

        String firstName,

        String lastName,

        UserRole userRole,

        LocalDate dateOfBirth,

        String idNumber,

        String birthNumber,

        String address
) { }
