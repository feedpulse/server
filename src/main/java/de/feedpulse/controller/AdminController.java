package de.feedpulse.controller;

import de.feedpulse.dto.response.ReferralCodeDTO;
import de.feedpulse.service.ReferralCodeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ReferralCodeService referralCodeService;

    public AdminController(ReferralCodeService referralCodeService) {
        this.referralCodeService = referralCodeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate-referral-code")
    public ReferralCodeDTO generateReferralCode() {
        return referralCodeService.createReferralCode();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/referral-codes")
    public List<ReferralCodeDTO> getUnusedReferralCodes() {
        return referralCodeService.getUnusedReferralCodes();
    }
}
