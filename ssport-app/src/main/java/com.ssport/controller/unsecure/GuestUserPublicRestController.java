package com.ssport.controller.unsecure;

import com.ssport.dto.users.GuestUserDTO;
import com.ssport.dto.users.GuestUserResponseDTO;
import com.ssport.service.users.GuestUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1")
@Api(value = "GuestUserPublicRestController", description = "Controller for guest users")
public class GuestUserPublicRestController {

    private final GuestUserService guestUserService;

    @Autowired
    public GuestUserPublicRestController(GuestUserService guestUserService) {
        this.guestUserService = guestUserService;
    }

    @PostMapping(value = "/guestUserLogin", produces = "application/json")
    @ApiOperation(value = "Logged in as a guest (no more than 5 times)", notes = "Available to unauthorized users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = GuestUserResponseDTO.class),
            @ApiResponse(code = 4007, message = "Sign-in count exceeded")
    })
    public OAuth2AccessToken guestUserLogin(@Valid @RequestBody GuestUserDTO guestUserDto) {

        return guestUserService.guestUserLogin(guestUserDto);
    }
}
