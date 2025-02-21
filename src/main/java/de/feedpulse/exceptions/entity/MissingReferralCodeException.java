package de.feedpulse.exceptions.entity;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingReferralCodeException extends BaseException {

    public MissingReferralCodeException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Missing referral code",
                "The referral code is missing.",
                "Check if the referral code is correct and try again."
        );
    }


}
