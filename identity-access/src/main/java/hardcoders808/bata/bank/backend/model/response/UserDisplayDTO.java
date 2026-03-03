package hardcoders808.bata.bank.backend.model.response;

import hardcoders808.bata.bank.backend.enums.UserRole;
import hardcoders808.bata.bank.backend.jpa.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserDisplayDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        String accountGroup,
        LocalDate dateOfBirth,
        String idNumber,
        String birthNumber,
        String address,
        LocalDateTime createdAt
) {

    public static UserDisplayDTO fromEntity(User user) {
        return new UserDisplayDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getAccountGroup(),
                user.getDateOfBirth(),
                user.getIdNumber(),
                user.getBirthNumber(),
                user.getAddress(),
                user.getCreatedAt()
        );
    }

    public static UserDisplayDTO fromDomain(final User user) {
        if (user == null) {
            return null;
        }

        return new UserDisplayDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getAccountGroup(),
                user.getDateOfBirth(),
                user.getIdNumber(),
                user.getBirthNumber(),
                user.getAddress(),
                user.getCreatedAt()
        );
    }
}