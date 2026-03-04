package hardcoders808.bata.bank.backend.model.request;

import hardcoders808.bata.bank.backend.enums.UserRole;
import hardcoders808.bata.bank.backend.jpa.domain.User;
import lombok.NonNull;

import java.time.LocalDate;

public record UpdateUserDTO(
        String email,
        String firstName,
        String lastName,
        UserRole role,
        String accountGroup,
        LocalDate dateOfBirth,
        String idNumber,
        String birthNumber,
        String address
) {
    public void updateEntity(final @NonNull User user) {
        user.setEmail(this.email());
        user.setFirstName(this.firstName());
        user.setLastName(this.lastName());
        user.setRole(this.role());
        user.setAccountGroup(this.accountGroup());
        user.setDateOfBirth(this.dateOfBirth());
        user.setIdNumber(this.idNumber());
        user.setBirthNumber(this.birthNumber());
        user.setAddress(this.address());
    }
}
