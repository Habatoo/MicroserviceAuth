package com.ssportup.emailsender.service.impl;

import com.ssportup.emailsender.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${base.url}")
    private String contextPath;
    @Value("${verify.email.path}")
    private String verifyEmailPath;
    @Value("${verify.email.by.link.path}")
    private String verifyEmailByLinkPath;
    @Value("${feedback.place.path}")
    private String placeFeedbackPath;
    @Value("${userplace.path}")
    private String userPlacePath;
    @Value("${happyhour.image.path}")
    private String happyHourImagePath;
    @Value("${from.email}")
    private String from;
    @Value("${from.name}")
    private String name;
    @Value("${reset.password.link}")
    private String resetPasswordLink;
    @Value("${admin.email.list}")
    private String[] adminEmails;
    @Value("${admin.email}")
    private String adminEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmailPasswordRecoveryCode(Map<String, String> message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        boolean multipart = true;

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, "utf-8");

            Context context = new Context();
            context.setVariable("fullName", message.get("fullName"));
            context.setVariable("code", message.get("code"));

            mimeMessage.setContent(templateEngine.process("passwordRecovery", context), "text/html");
            helper.setTo(message.get("email"));
            helper.setSubject("Password recovery");
            helper.setFrom(new InternetAddress(from, name));

            mailSender.send(mimeMessage);

            LOGGER.info("A password recovery link has been sent to " + message.get("email"));
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void sendEmailVerificationCode(Map<String, String> message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

            String url = contextPath + verifyEmailByLinkPath + "?email=" + message.get("email") + "&code=" + message.get("verificationCode");

            Context context = new Context();
            context.setVariable("fullName", message.get("fullName"));
            context.setVariable("verificationUrl", url);
            context.setVariable("code", message.get("verificationCode"));

            mimeMessage.setContent(templateEngine.process("verificationCode", context), "text/html");
            helper.setTo(message.get("email"));
            helper.setSubject("Pryme Time email verification");
            helper.setFrom(new InternetAddress(from, name));

            mailSender.send(mimeMessage);

            LOGGER.info("A email verification message has been sent to " + message.get("email"));
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void sendEmailVerificationAndPasswordResetCode(Map<String, String> message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

            String url = contextPath + verifyEmailByLinkPath + "?email=" + message.get("email") + "&code=" + message.get("verificationCode");

            Context context = new Context();
            context.setVariable("fullName", message.get("fullName"));
            context.setVariable("verificationCode", message.get("verificationCode"));
            context.setVariable("verificationUrl", url);
            context.setVariable("passwordResetToken", message.get("passwordResetToken"));

            mimeMessage.setContent(templateEngine.process("verificationAndPasswordResetCode", context), "text/html");
            helper.setTo(message.get("email"));
            helper.setSubject("Pryme Time email verification and password reset");
            helper.setFrom(new InternetAddress(from, name));

            mailSender.send(mimeMessage);

            LOGGER.info("A email verification message has been sent to " + message.get("email"));
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void sendEmailPasswordResetCode(Map<String, String> message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        boolean multipart = true;

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, "utf-8");
            String url = contextPath + resetPasswordLink + "?email=" + message.get("email") + "&code=" + message.get("code");

            Context context = new Context();
            context.setVariable("fullName", message.get("fullName"));
            context.setVariable("verificationUrl", url);
            context.setVariable("code", message.get("code"));

            mimeMessage.setContent(templateEngine.process("passwordReset", context), "text/html");
            helper.setTo(message.get("email"));
            helper.setSubject("Password recovery");
            helper.setFrom(new InternetAddress(from, name));

            mailSender.send(mimeMessage);

            LOGGER.info("A password recovery link has been sent to " + message.get("email"));
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

}