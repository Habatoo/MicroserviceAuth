package com.ssport.service.users;

import com.ssport.dto.users.GuestUserDTO;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public interface GuestUserService {

    OAuth2AccessToken guestUserLogin(GuestUserDTO guestUserDto);

}
