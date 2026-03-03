package hardcoders808.backend.resource;

import hardcoders808.backend.model.request.UserRegistrationRequestDTO;
import hardcoders808.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Resource
@RequestMapping("/users")
public class UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserRegistrationRequestDTO request) {
        log.info("Registration attempt for email: {}", request.email());

        this.userService.registerUser(request);

        log.info("User successfully registered: {}", request.email());
    }
}
