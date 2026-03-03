package hardcoders808.bata.bank.backend.resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import hardcoders808.bata.bank.backend.model.request.LoginRequest;
import hardcoders808.bata.bank.backend.model.request.MfaVerifyRequest;
import hardcoders808.bata.bank.backend.model.response.LoginResponse;
import hardcoders808.bata.bank.backend.service.AuthService;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:08
 */
@Slf4j
@RestController
@RequestMapping("${endpoint.api.auth-resource}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthResource {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest request, final HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticate(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(final HttpServletRequest request, final HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {
        authService.logout(request, response);
    }

    @PostMapping("/verify-login")
    public LoginResponse verify(final @Valid @RequestBody MfaVerifyRequest request,
                                final HttpServletRequest httpRequest,
                                final HttpServletResponse httpResponse) {
        return authService.verifyMfaAndIssueTokens(request, httpRequest, httpResponse);
    }
}
