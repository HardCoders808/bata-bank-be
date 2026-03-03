package hardcoders808.bata.bank.backend.security.exception;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tools.jackson.databind.ObjectMapper;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 14:07
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(final @NonNull HttpServletRequest request,
                         final @NonNull HttpServletResponse response,
                         final @NonNull AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized request from [{}]", request.getRequestURL().toString(), authException);

        final var status = HttpServletResponse.SC_UNAUTHORIZED;

        final var error = "Unauthorized";
        final var path = request.getRequestURI();

        final String message;
        final String code;

        if (authException instanceof LockedException) {
            log.debug("User cannot be authorized: user is blocked.");
            message = "User is blocked";
            code = "USER_LOCKED";
        } else if (authException instanceof BadCredentialsException) {
            log.debug("User cannot be authorized: bad credentials.");
            message = "Wrong username or password";
            code = "BAD_CREDENTIALS";
        } else {
            log.debug("User cannot be authorized, exception: [{}]", authException.getMessage(), authException);
            message = "Authentication required";
            code = "AUTH_REQUIRED";
        }

        final var body = new LinkedHashMap<String, Object>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("code", code);
        body.put("message", message);
        body.put("path", path);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
