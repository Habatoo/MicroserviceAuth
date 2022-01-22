package com.ssport.controller.unsecure;

import com.ssport.dto.users.*;
import com.ssport.service.users.UserService;
import com.ssport.util.Util;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Validated
@RestController
@RequestMapping("/api/v1")
@Api(value = "GuestUserPublicRestController", description = "Controller for unauthorized users")
public class UserPublicRestController {

    private final UserService userService;

    @Autowired
    public UserPublicRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/registerWithEmail", produces = "application/json")
    @ApiOperation(value = "Register User", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4001, message = "User already exists")
    })
    public ResponseEntity<Object> registerUser(@Valid @RequestBody NewUserDTO newUserDTO) {

        return Util.createResponseEntity(userService.registerNewUser(newUserDTO));
    }

    @PostMapping(value = "/loginWithEmail", produces = "application/json")
    @ApiOperation(value = "Login with email", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4029, message = "Bad credentials"),
            @ApiResponse(code = 4029, message = "Invalid username or password"),
            @ApiResponse(code = 4032, message = "Email is not verified"),
    })
    public ResponseEntity<Object> loginWithEmail(@Valid @RequestBody LoginDTO loginDto) {

        return Util.createResponseEntity(userService.loginWithEmail(loginDto));
    }

    @PostMapping(value = "/passwordRecovery", produces = "application/json")
    @ApiOperation(value = "Reset Password. Sending a password recovery code", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 4002, message = "User not found")
    })
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody PasswordRecoveryDTO passwordRecoveryDTO) {
        userService.passwordRecovery(passwordRecoveryDTO.getEmail());

        return Util.createResponseEntity("A password recovery code has been sent to " + passwordRecoveryDTO.getEmail());
    }

    @PostMapping(value = "/changePassword", produces = "application/json")
    @ApiOperation(value = "Change Password.", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 4002, message = "User not found"),
            @ApiResponse(code = 4006, message = "Code not found"),
            @ApiResponse(code = 4005, message = "Token usage time has expired"),
            @ApiResponse(code = 4007, message = "Number of attempts to enter the correct code exceeded. Repeat the password recovery procedure again."),
            @ApiResponse(code = 4008, message = "You entered the wrong code")
    })
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {

        return Util.createResponseEntity(userService.changePassword(changePasswordDTO));
    }

    @PostMapping(value = "/completeUpdate", produces = "application/json")
    @ApiOperation(value = "Verify email and set password.", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 4002, message = "User not found"),
            @ApiResponse(code = 4006, message = "Code not found"),
            @ApiResponse(code = 4005, message = "Token usage time has expired"),
            @ApiResponse(code = 4007, message = "Number of attempts to enter the correct code exceeded. Repeat the password recovery procedure again."),
            @ApiResponse(code = 4008, message = "You entered the wrong code")
    })
    public ResponseEntity<Object> verifyEmailAndChange(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {

        return Util.createResponseEntity(userService.completeUpdate(changePasswordDTO));
    }

    @GetMapping(value = "/sendVerificationCode", produces = "application/json")
    @ApiOperation(value = "Resend verification code", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 4002, message = "User not found")
    })
    public ResponseEntity<Object> getVerificationCode(@RequestParam(name = "email")
                                                      @ApiParam(value = "email", required = true)
                                                              String email) {
        userService.getVerificationCode(email);

        return Util.createResponseEntity("Verification code has been sent to your email ");
    }

    @PostMapping(value = "/verifyEmail", produces = "application/json")
    @ApiOperation(value = "Check verification code", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 4002, message = "User not found"),
            @ApiResponse(code = 4007, message = "Number of attempts to enter the correct code exceeded. Repeat the verification code procedure again.")
    })
    public ResponseEntity<Object> checkVerificationCode(@Valid @RequestBody VerificationCodeDto verificationCodeDto) {

        return Util.createResponseEntity(userService.checkVerificationCode(verificationCodeDto));
    }

    @GetMapping(value = "/verifyEmailByLink", produces = "application/json")
    @ApiOperation(value = "Check verification code", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 4002, message = "User not found"),
            @ApiResponse(code = 4007, message = "Number of attempts to enter the correct code exceeded. Repeat the verification code procedure again.")
    })
    public ResponseEntity<Object> verifyEmail(@RequestParam(name = "email")
                                              @ApiParam(value = "email", required = true)
                                                      String email,
                                              @RequestParam(name = "code")
                                              @ApiParam(value = "code", required = true)
                                                      String code) {

        VerificationCodeDto verificationCodeDto = new VerificationCodeDto();
        verificationCodeDto.setEmail(email);
        verificationCodeDto.setCode(code);
        return Util.createResponseEntity(userService.checkVerificationCode(verificationCodeDto));
    }

    @PostMapping(value = "/changeEmail", produces = "application/json")
    @ApiOperation(value = "Change your email address during registration", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4002, message = "User not found"),
            @ApiResponse(code = 4003, message = "Email is already in use")
    })
    public ResponseEntity<Object> changeEmail(@Valid @RequestBody ChangeEmailDto changeEmailDto) {

        return Util.createResponseEntity(userService.changeEmail(changeEmailDto));
    }

    @PostMapping(value = "/registerSocial", produces = "application/json")
    @ApiOperation(value = "Login using social networks", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4009, message = "Invalid token_id"),
            @ApiResponse(code = 4010, message = "Invalid provider id")
    })
    public ResponseEntity<Object> socialRegister(@Valid @RequestBody NewSocialUserDto newSocialUserDto) throws GeneralSecurityException, IOException {

        return Util.createResponseEntity(userService.socialRegister(newSocialUserDto));
    }

}
