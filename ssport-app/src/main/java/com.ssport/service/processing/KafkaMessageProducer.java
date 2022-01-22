package com.ssport.service.processing;

import com.ssport.model.users.PasswordResetToken;
import com.ssport.model.users.User;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageProducer.class);
    private final KafkaSender<String, Object> kafkaSender;
    @Value("${spring.kafka.email.password.recovery.topic}")
    private String emailPasswordRecoveryTopic;
    @Value("${spring.kafka.email.email.verification.code.topic}")
    private String emailVerificationCodeTopic;
    @Value("${spring.kafka.email.feedback.topic}")
    private String emailFeedbackTopic;
    @Value("${spring.kafka.email.feedback.place.topic}")
    private String emailPlaceFeedbackTopic;
    @Value("${spring.kafka.email.user.place.admin.topic}")
    private String userPlaceAdminTopic;
    @Value("${spring.kafka.email.user.place.topic}")
    private String userPlaceTopic;
    @Value("${spring.kafka.email.new.image.topic}")
    private String emailNewImageTopic;
    @Value("${spring.kafka.scraper.list.request.topic}")
    private String scraperListRequestTopic;
    @Value("${spring.kafka.scraper.place.request.topic}")
    private String scraperPlaceRequestTopic;
    @Value("${spring.kafka.email.email.verification.and.password.reset.code.topic}")
    private String emailVerifyAndPasswordResetTopic;
    @Value("${spring.kafka.email.password.reset.topic}")
    private String emailPasswordResetTopic;

    @Autowired
    public KafkaMessageProducer(KafkaSender<String, Object> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    public void sendEmailPasswordResetCode(PasswordResetToken passwordResetToken) {
        Map<String, String> message = new HashMap<>();
        message.put("userId", passwordResetToken.getUser().getId().toString());
        message.put("email", passwordResetToken.getUser().getEmail());
        message.put("code", passwordResetToken.getToken());
        message.put("fullName", passwordResetToken.getUser().getFullName());

        sendData(emailPasswordResetTopic, message, Map.class);
    }

    public void sendEmailVerificationAndPasswordResetCode(PasswordResetToken passwordResetToken, String verificationCode) {
        Map<String, String> message = new HashMap<>();
        message.put("userId", passwordResetToken.getUser().getId().toString());
        message.put("email", passwordResetToken.getUser().getEmail());
        message.put("verificationCode", verificationCode);
        message.put("passwordResetToken", passwordResetToken.getToken());
        message.put("fullName", passwordResetToken.getUser().getFullName());

        sendData(emailVerifyAndPasswordResetTopic, message, Map.class);
    }


    public void sendEmailPasswordRecoveryCode(PasswordResetToken passwordResetToken) {

        Map<String, String> message = new HashMap<>();
        message.put("userId", passwordResetToken.getUser().getId().toString());
        message.put("email", passwordResetToken.getUser().getEmail());
        message.put("code", passwordResetToken.getToken());
        message.put("fullName", passwordResetToken.getUser().getFullName());

        sendData(emailPasswordRecoveryTopic, message, Map.class);
    }

    public void sendEmailVerificationCode(User user, String verificationCode) {
        Map<String, String> message = new HashMap<>();
        message.put("userId", user.getId().toString());
        message.put("email", user.getEmail());
        message.put("fullName", user.getFullName());
        message.put("verificationCode", verificationCode);

        sendData(emailVerificationCodeTopic, message, Map.class);
    }

    private void sendData(String kafkaTopic, Object value, Class clazz) {
        kafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<>(kafkaTopic, value), clazz.getSimpleName())))
                .doOnError(e -> LOGGER.error("Send " + clazz.getSimpleName() + " failed", e))
                .subscribe(r -> LOGGER.info(String.format("Message %s sent successfully, topic-partition=%s-%d offset=%d",
                        r.correlationMetadata(),
                        r.recordMetadata().topic(),
                        r.recordMetadata().partition(),
                        r.recordMetadata().offset())));
    }
}
