package de.feedpulse.service;

import de.feedpulse.dto.response.ReferralCodeDTO;
import de.feedpulse.model.ReferralCode;
import de.feedpulse.model.User;
import de.feedpulse.repository.ReferralCodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReferralCodeService {

    private final ReferralCodeRepository referralCodeRepository;

    public ReferralCodeService(ReferralCodeRepository referralCodeRepository) {
        this.referralCodeRepository = referralCodeRepository;
    }

    public Optional<ReferralCode> findByCode(String code) {
        Optional<ReferralCode> referralCode = referralCodeRepository.findByCode(code);
        if (isCodeValid(referralCode)) return referralCode;
        return Optional.empty();
    }

    private boolean isCodeValid(Optional<ReferralCode> referralCode) {
        return referralCode.isPresent() && !referralCode.get().isUsed() && referralCode.get().getDateExpired().isAfter(LocalDate.now());
    }

    public ReferralCodeDTO createReferralCode() {
        String code;
        ReferralCode referralCode;

        do {
            code = UUID.randomUUID().toString();
            referralCode = new ReferralCode(code, LocalDate.now().plusDays(7));
        } while (findByCode(code).isPresent());

        referralCode = referralCodeRepository.save(referralCode);
        return ReferralCodeDTO.of(referralCode);
    }

    public void invalidateReferralCode(ReferralCode referralCode, User usedBy) {
        referralCode.setUsed(true);
        referralCode.setDateUsed(LocalDate.now());
        referralCode.setUsedBy(usedBy);
        referralCodeRepository.save(referralCode);
    }

    public List<ReferralCodeDTO> getUnusedReferralCodes() {
        // find all referral codes whic are not used
        return referralCodeRepository.findByIsUsedIsTrue().stream()
                .map(ReferralCodeDTO::of)
                .toList();
    }
}
