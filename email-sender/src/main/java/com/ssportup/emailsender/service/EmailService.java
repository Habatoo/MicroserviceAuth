package com.ssportup.emailsender.service;

import java.util.Map;

public interface EmailService {

    void sendEmailPasswordRecoveryCode(Map<String, String> message);

    void sendEmailVerificationCode(Map<String, String> message);

    void sendEmailVerificationAndPasswordResetCode(Map<String, String> message);

    void sendEmailPasswordResetCode(Map<String, String> message);

}
