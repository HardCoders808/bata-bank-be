package hardcoders808.bata.bank.backend.model.response;

import java.time.LocalDate;

import hardcoders808.bata.bank.backend.enums.UserRole;
import hardcoders808.bata.bank.backend.jpa.domain.User;

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


    public static UserRegistrationResponseDTO fromDomain(final User user) {
        return new UserRegistrationResponseDTO(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getDateOfBirth(),
                user.getIdNumber(),
                user.getBirthNumber(),
                user.getAddress()
        );
    }
}
