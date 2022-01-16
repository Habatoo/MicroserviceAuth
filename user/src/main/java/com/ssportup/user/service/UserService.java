package com.ssportup.user.service;

import com.ssportup.user.dto.user.*;
import com.ssportup.user.model.user.User;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.UUID;

public interface UserService {

    boolean validatePassword(User user, String passwordToCheck);

    UserDataResponseDTO registerNewUser(NewUserDTO newUserDTO);

    UserDataResponseDTO loginWithEmail(LoginDTO loginDto);

    UserDataResponseDTO getProfile(Authentication authentication);

    UserDataResponseDTO getUserProfile(UUID userId);

    void passwordRecovery(String email);

    void getVerificationCode(String email);

    Map checkVerificationCode(VerificationCodeDTO verificationCodeDto);

    UserDataResponseDTO socialRegister(NewSocialUserDTO socialUserDto) throws GeneralSecurityException, IOException;

    void deleteUserProfile(UUID userId);

    UserDataResponseDTO createUserProfile(NewUserDTO newUserDTO);

    TokenResponse appleAuth(String authorizationCodeSentByApp) throws Exception;

}
