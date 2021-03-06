package ogorek.wojciech.service.services;

import ogorek.wojciech.persistence.exception.AppException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public final class EmailService {
    public EmailService() {
    }

    private static final String EMAIL_ADDRESS = "<sender_email>";
    private static final String EMAIL_PASSWORD = "<password>";

    public static void send(String to, String title, String html) {
        try {
            System.out.println("SENDING EMAIL MESSAGE ...");
            Session session = createSession();
            MimeMessage mimeMessage = new MimeMessage(session);
            prepareEmailMessage(mimeMessage, to, title, html);
            Transport.send(mimeMessage);
            System.out.println("EMAIL MESSAGE HAS BEEN SENT ...");
        } catch (Exception e) {
            throw new AppException("Sending an email failed " + e.getMessage());
        }

    }

    private static void prepareEmailMessage(MimeMessage mimeMessage, String to, String title, String html) {
        try {
            mimeMessage.setContent(html, "text/html; charset=utf-8");
            mimeMessage.setFrom(new InternetAddress(EMAIL_ADDRESS));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(title);
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
    }

    private static Session createSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
            }
        });
    }
}
