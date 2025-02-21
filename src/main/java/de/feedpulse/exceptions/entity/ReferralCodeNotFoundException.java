package de.feedpulse.exceptions.entity;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReferralCodeNotFoundException extends BaseException {
    public ReferralCodeNotFoundException(String referralCode) {
        super(
                HttpStatus.NOT_FOUND.value(),
                "Referral code not found in db: " + referralCode,
                "Check if the referral code exists in the db and try again.",
                "Check if the referral code exists in the db and try again."
        );
    }
}
