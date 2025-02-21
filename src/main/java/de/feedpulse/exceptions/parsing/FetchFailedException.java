package de.feedpulse.exceptions.parsing;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FetchFailedException extends BaseException {

    public FetchFailedException(String feedUrl) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to fetch content",
                "Failed to fetch content from url: " + feedUrl,
                "Please check if the url is correct and try again."
        );
    }


}
