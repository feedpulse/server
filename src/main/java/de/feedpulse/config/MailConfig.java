package de.feedpulse.config;


import lombok.Getter;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class MailConfig {

    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Getter
    @Value("${spring.mail.username}")
    private String userName;

    @Value("${spring.mail.password}")
    private String passwd;


    @Bean
    public Mailer getMailBuilder() {
        return MailerBuilder
                .withSMTPServer(host, port, userName, passwd)
//                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer();
    }

//    @Bean
//    public JavaMailSender getJavaMailSender() {
//        System.out.println("host: " + host);
//        System.out.println("passwd: " + passwd);
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost(host);
//        mailSender.setPort(port);
//
//        mailSender.setUsername(userName);
//        mailSender.setPassword(passwd);
//
////        Properties props = mailSender.getJavaMailProperties();
////        props.put("mail.transport.protocol", "smtp");
////
////        props.put("mail.smtp.auth", "true");
////        props.put("spring.mail.properties.mail.smtp.auth", "true");
////
////        props.put("mail.smtp.starttls.enable", "true");
////        props.put("spring.mail.properties.mail.smtp.starttls.enable", "true");
////
////        props.put("mail.debug", "true");
////
////        // creates a new session with an authenticator
////        Authenticator auth = new Authenticator() {
////            public PasswordAuthentication getPasswordAuthentication() {
////                return new PasswordAuthentication(userName, passwd);
////            }
////        };
////
////        Session session = Session.getInstance(props, auth);
////        mailSender.setSession(session);
//
//        return mailSender;
//    }
}
