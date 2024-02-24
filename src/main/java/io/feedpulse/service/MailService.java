package io.feedpulse.service;

import io.feedpulse.config.MailConfig;
import io.feedpulse.model.User;
import jakarta.annotation.PostConstruct;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.Recipient;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final MailConfig mailConfig;

    private final Mailer mailer;

    private final UserService userService;

    private List<String> adminEmails;

    public MailService(MailConfig mailConfig, Mailer mailer, @Lazy UserService userService) {
        this.mailConfig = mailConfig;
        this.mailer = mailer;
        this.userService = userService;
    }

    @PostConstruct
    public void postConstruct() {
        /// how to update if the admin list changes at runtime?
        // refresh on every send email? seems pretty inefficient...
        // maybe a scheduled task to refresh the list every hour?
        this.adminEmails = userService.getAllAdmins().stream().map(User::getEmail).toList();
    }

    private void sendMail(Email email) {
        try {
            mailer.sendMail(email);
        } catch (Exception e) {
            log.error("Error sending mail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendAccountRequestMail(String to) {
        Email email = EmailBuilder.startingBlank()
                .from("Feedpulse", mailConfig.getUserName())
                .to(to)
                .withSubject("Account Request")
                .withPlainText("Your account request has been received. We will get back to you soon.")
                .withHTMLText("<p>Your account request has been received. We will get back to you soon.</p>")
                .buildEmail();
        sendMail(email);
    }

    public void sendAccountRequestMailToAdmin(User user) {
        Email email = EmailBuilder.startingBlank()
                .from("Feedpulse", mailConfig.getUserName())
                .toMultiple(adminEmails)
                .withSubject("New Account Request")
                .withPlainText("A new account request has been received from " + user.getEmail())
                .withHTMLText("<p>A new account request has been received from " + user.getEmail() + "</p>")
                .buildEmail();
        sendMail(email);
    }

    public void sendAccountRequestSuccessfulMail() {
        Email email = EmailBuilder.startingBlank()
                .from("Feedpulse", mailConfig.getUserName())
                .toMultiple(adminEmails)
                .withSubject("Account Request Processed")
                .withPlainText("Your account request has been approved. You can now login.")
                .withHTMLText("<p>Your account request has been approved. You can now login.</p>")
                .buildEmail();
        sendMail(email);
    }
}
