package hardcoders808.bata.bank.backend.service;

import hardcoders808.bata.bank.backend.jpa.domain.User;
import hardcoders808.bata.bank.backend.model.request.UserRegistrationRequestDTO;
import hardcoders808.bata.bank.backend.model.response.UserRegistrationResponseDTO;
import hardcoders808.bata.bank.backend.jpa.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO request) {

        final String rawPassword = request.password();
        final String encodedPassword = passwordEncoder.encode(rawPassword);

        final User user = User.builder()
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

        log.info("User Created: {}", user);

        this.userRepository.save(user);

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
