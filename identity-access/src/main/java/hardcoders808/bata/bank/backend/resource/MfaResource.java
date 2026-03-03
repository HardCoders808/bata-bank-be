package hardcoders808.bata.bank.backend.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import hardcoders808.bata.bank.backend.model.request.MfaConfirmRequest;
import hardcoders808.bata.bank.backend.model.response.MfaEnrollResponse;
import hardcoders808.bata.bank.backend.service.MfaService;

/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 21:11
 */
@RestController
@RequestMapping("${endpoint.api.mfa-resource}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MfaResource {
    private final MfaService mfaService;

    //used for enabling mfa
    @PostMapping("/enroll")
    public MfaEnrollResponse enroll(final Authentication authentication) {
        return mfaService.enrollTotp(authentication);
    }

    // to verify mfa enrolment
    @PostMapping("/confirm")
    public void confirm(@RequestBody MfaConfirmRequest request, Authentication authentication) {
        mfaService.confirmEnrollment(request, authentication);
    }

}
