package com.ssport.service.users;

import com.ssport.dto.users.*;
import com.ssport.model.users.User;
import com.ssport.pagination.CustomPage;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface UserService {

    boolean validatePassword(User user, String passwordToCheck);

    UserDataResponseDTO registerNewUser(NewUserDTO newUserDTO);

    UserDataResponseDTO loginWithEmail(LoginDTO loginDto);

    UserDataResponseDTO getProfile(Authentication authentication);

    UserDataResponseDTO getUserProfile(UUID userId);

    UserDataResponseDTO updatePassword(Authentication authentication, UpdatePasswordDTO updatePasswordDto);

    UserDataResponseDTO updateProfile(Authentication authentication, UpdateProfileDTO updateProfileDto);

    void passwordRecovery(String email);

    String changePassword(ChangePasswordDTO changePasswordDTO);

    void getVerificationCode(String email);

    Map checkVerificationCode(VerificationCodeDto verificationCodeDto);

    UserDataResponseDTO changeEmail(ChangeEmailDto changeEmailDto);

    UserDataResponseDTO socialRegister(NewSocialUserDto socialUserDto) throws GeneralSecurityException, IOException;

    void deleteUserProfile(UUID userId);

    UserDataResponseDTO updateUserProfile(UUID userId, UpdateProfileDTO profileDTO);

    UserDataResponseDTO createUserProfile(NewUserDTO newUserDTO);

    String completeUpdate(ChangePasswordDTO ChangePasswordDTO);

    void resetPassword(PasswordResetDTO passwordResetDTO);

    TokenResponse appleAuth(String authorizationCodeSentByApp) throws Exception;
}
