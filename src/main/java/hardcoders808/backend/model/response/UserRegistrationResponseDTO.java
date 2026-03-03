package hardcoders808.backend.model.response;

import hardcoders808.backend.enums.UserRole;

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
