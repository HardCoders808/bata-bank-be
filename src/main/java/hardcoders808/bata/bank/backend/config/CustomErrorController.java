package hardcoders808.bata.bank.backend.config;

import java.time.Instant;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import lombok.RequiredArgsConstructor;

import hardcoders808.bata.bank.backend.model.ApiErrorResponse;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:58
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CustomErrorController implements ErrorController {
    private final ErrorAttributes errorAttributes;

    @RequestMapping("${server.servlet.context-path}/error")
    public ResponseEntity<ApiErrorResponse> handleError(final HttpServletRequest request) {

        final var statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        final var status = statusCode != null
                ? HttpStatus.valueOf(statusCode)
                : HttpStatus.INTERNAL_SERVER_ERROR;

        final var attributes = errorAttributes.getErrorAttributes(
                new ServletWebRequest(request),
                ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS
                )
        );

        final var message = (String) attributes.getOrDefault("message", "Unexpected error");

        final var response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)
        );

        return ResponseEntity.status(status).body(response);
    }
}
