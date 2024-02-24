package io.feedpulse.dto.response;

import io.feedpulse.model.ReferralCode;

import java.time.LocalDate;

public record ReferralCodeDTO(String code, LocalDate dateCreated, LocalDate dateExpired, LocalDate dateUsed, boolean isUsed) {

    public static ReferralCodeDTO of(ReferralCode referralCode) {
        return new ReferralCodeDTO(
                referralCode.getCode(),
                referralCode.getDateCreated(),
                referralCode.getDateExpired(),
                referralCode.getDateUsed(),
                referralCode.isUsed()
        );
    }

}
