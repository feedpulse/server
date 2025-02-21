package de.feedpulse.exceptions.entity;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidReferralCodeException extends BaseException {
    public InvalidReferralCodeException(String referralCode) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid referral code: " + referralCode,
                "The referral code you entered is invalid.",
                "Check if the referral code is correct and try again."
        );
    }
}
