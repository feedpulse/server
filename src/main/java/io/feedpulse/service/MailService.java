package io.feedpulse.service;

import io.feedpulse.config.MailConfig;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final MailConfig mailConfig;

    private final Mailer mailer;

    public MailService(MailConfig mailConfig, Mailer mailer) {
        this.mailConfig = mailConfig;
        this.mailer = mailer;
    }

    public void sendAccountRequestMail(String to) {
        Email email = EmailBuilder.startingBlank()
                .from("Feedpulse", mailConfig.getUserName())
                .to(to)
                .withSubject("Account Request")
                .withPlainText("Your account request has been received. We will get back to you soon.")
                .withHTMLText("<p>Your account request has been received. We will get back to you soon.</p>")
                .buildEmail();
        try {
            mailer.sendMail(email);
        } catch (Exception e) {
            log.error("Error sending mail: " + e.getMessage());
            e.printStackTrace();

        }
    }

}
