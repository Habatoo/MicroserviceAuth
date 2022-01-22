package com.ssport.controller.secure;

import com.ssport.dto.users.*;
import com.ssport.service.users.UserService;
import com.ssport.util.Util;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Collections;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/secure")
@Api(value = "UserRestController", description = "Controller for registered users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_REGISTERED_USER," +
            "T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR," +
            "T(com.prymetime.util.Constants).ROLE_SOCIAL_USER," +
            "T(com.prymetime.util.Constants).ROLE_RESTAURANT_USER)")
    @GetMapping(value = "/profile", produces = "application/json")
    @ApiOperation(value = "Get Profile", notes = "hasAnyRole('ROLE_REGISTERED_USER', 'ROLE_SOCIAL_USER', 'ROLE_RESTAURANT_USER', 'ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid")
    })
    public ResponseEntity<Object> getProfile(@ApiIgnore Authentication authentication) {

        return Util.createResponseEntity(userService.getProfile(authentication));
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR)")
    @GetMapping(value = "/profile/{userId}", produces = "application/json")
    @ApiOperation(value = "Get User Profile", notes = "hasAnyRole('ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4030, message = "Access is denied"),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid"),
            @ApiResponse(code = 4002, message = "User not found")
    })
    public ResponseEntity<Object> getUserProfile(@PathVariable
                                                 @ApiParam(value = "User Id", required = true)
                                                         UUID userId) {

        return Util.createResponseEntity(userService.getUserProfile(userId));
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_REGISTERED_USER," +
            "T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR," +
            "T(com.prymetime.util.Constants).ROLE_RESTAURANT_USER)")
    @PostMapping(value = "/updatePassword", produces = "application/json")
    @ApiOperation(value = "Update Password", notes = "hasAnyRole('ROLE_REGISTERED_USER', 'ROLE_RESTAURANT_USER', 'ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4030, message = "Access is denied"),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid"),
            @ApiResponse(code = 4004, message = "Old password is wrong")
    })
    public ResponseEntity<Object> updatePassword(@ApiIgnore Authentication authentication,
                                                 @Valid @RequestBody UpdatePasswordDTO updatePasswordDto) {

        return Util.createResponseEntity(userService.updatePassword(authentication, updatePasswordDto));
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR)")
    @PostMapping(value = "/resetPassword", produces = "application/json")
    @ApiOperation(value = "Reset user's password", notes = "hasAnyRole('ROLE_REGISTERED_USER', 'ROLE_RESTAURANT_USER', 'ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 4030, message = "Access is denied"),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid"),
            @ApiResponse(code = 4002, message = "User not found")
    })
    public ResponseEntity<Object> resetPassword(@ApiIgnore Authentication authentication,
                                                @Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        userService.resetPassword(passwordResetDTO);
        return Util.createResponseEntity(Collections.EMPTY_MAP);
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_REGISTERED_USER," +
            "T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR," +
            "T(com.prymetime.util.Constants).ROLE_RESTAURANT_USER)")
    @PostMapping(value = "/profile", produces = "application/json")
    @ApiOperation(value = "Update Profile", notes = "hasAnyRole('ROLE_REGISTERED_USER', 'ROLE_RESTAURANT_USER', 'ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 4030, message = "Access is denied"),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid"),
            @ApiResponse(code = 4003, message = "Email already taken. Try another")
    })
    public ResponseEntity<Object> updateProfile(@ApiIgnore Authentication authentication,
                                                @Valid @RequestBody UpdateProfileDTO updateProfileDto) {

        return Util.createResponseEntity(userService.updateProfile(authentication, updateProfileDto));
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR)")
    @DeleteMapping(value = "/profile/{userId}", produces = "application/json")
    @ApiOperation(value = "Delete User Profile", notes = "hasAnyRole('ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 4030, message = "Access is denied"),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid"),
            @ApiResponse(code = 4002, message = "User not found")
    })
    public ResponseEntity<Object> deleteUserProfile(@PathVariable
                                                    @ApiParam(value = "User Id", required = true)
                                                            UUID userId) {
        userService.deleteUserProfile(userId);
        return Util.createResponseEntity(Collections.EMPTY_MAP);
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR)")
    @PostMapping(value = "/profile/{userId}", produces = "application/json")
    @ApiOperation(value = "Update User Profile", notes = "hasAnyRole('ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4030, message = "Access is denied"),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid"),
            @ApiResponse(code = 4002, message = "User not found")
    })
    public ResponseEntity<Object> updateUserProfile(@PathVariable
                                                    @ApiParam(value = "User Id", required = true) UUID userId,
                                                    @Valid @RequestBody UpdateProfileDTO updateProfileDto) {
        return Util.createResponseEntity(userService.updateUserProfile(userId, updateProfileDto));
    }

    @PreAuthorize("hasAnyRole(T(com.prymetime.util.Constants).ROLE_ADMINISTRATOR)")
    @PostMapping(value = "/profile/create", produces = "application/json")
    @ApiOperation(value = "Create User Profile", notes = "hasAnyRole('ROLE_ADMINISTRATOR')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDataResponseDTO.class),
            @ApiResponse(code = 4030, message = "Access is denied"),
            @ApiResponse(code = 4033, message = "Invalid access token"),
            @ApiResponse(code = 4034, message = "Access token expired"),
            @ApiResponse(code = 4035, message = "Client not valid"),
            @ApiResponse(code = 4002, message = "User not found")
    })
    public ResponseEntity<Object> createUserProfile(@Valid @RequestBody NewUserDTO newUserDTO) {
        return Util.createResponseEntity(userService.createUserProfile(newUserDTO));
    }

}
