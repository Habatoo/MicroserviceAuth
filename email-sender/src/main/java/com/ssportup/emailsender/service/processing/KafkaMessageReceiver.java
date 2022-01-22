package com.ssportup.emailsender.service.processing;

import com.ssportup.emailsender.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaMessageReceiver {

    private final EmailService emailService;

    @Autowired
    public KafkaMessageReceiver(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${spring.kafka.email.password.reset.topic}")
    public void listenEmailPasswordResetTopic(@Payload Map<String, String> message) {
        emailService.sendEmailPasswordResetCode(message);
    }

    @KafkaListener(topics = "${spring.kafka.email.email.verification.and.password.reset.code.topic}")
    public void listenEmailEmailVerificationAndPasswordResetTopic(@Payload Map<String, String> message) {
        emailService.sendEmailVerificationAndPasswordResetCode(message);
    }

    @KafkaListener(topics = "${spring.kafka.email.password.recovery.topic}")
    public void listenEmailPasswordRecoveryTopic(@Payload Map<String, String> message) {
        emailService.sendEmailPasswordRecoveryCode(message);
    }

    @KafkaListener(topics = "${spring.kafka.email.email.verification.code.topic}")
    public void listenEmailVerificationCodeTopic(@Payload Map<String, String> message) {
        emailService.sendEmailVerificationCode(message);
    }

}
