package com.ssport.service.users.impl;

import com.ssport.dto.users.GuestUserDTO;
import com.ssport.exception.ErrorCode;
import com.ssport.exception.SportAppException;
import com.ssport.model.users.GuestUser;
import com.ssport.repository.datajpa.GuestUserRepository;
import com.ssport.repository.datajpa.RoleRepository;
import com.ssport.service.tokens.CustomTokenService;
import com.ssport.service.users.GuestUserService;
import com.ssport.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GuestUserServiceImpl implements GuestUserService {

    private final RoleRepository roleRepository;

    private final GuestUserRepository guestUserRepository;

    private final CustomTokenService customTokenService;

    @Autowired
    public GuestUserServiceImpl(RoleRepository roleRepository, GuestUserRepository guestUserRepository, CustomTokenService customTokenService) {
        this.roleRepository = roleRepository;
        this.guestUserRepository = guestUserRepository;
        this.customTokenService = customTokenService;
    }

    @Override
    @Transactional
    public OAuth2AccessToken guestUserLogin(GuestUserDTO guestUserDto) {
        Optional<GuestUser> guestUserOpt = guestUserRepository.findById(guestUserDto.getToken());

        if(guestUserOpt.isPresent()) {
            GuestUser guestUser = guestUserOpt.get();
            if(guestUser.getLoginCount() >= Constants.GUEST_USER_LOGIN_MAX_COUNT) {

                throw new SportAppException(ErrorCode.NUMBER_OF_ATTEMPTS_EXCEEDED, "Sign-in count exceeded");
            } else {
                guestUser.setLoginCount(guestUser.getLoginCount()+1);
                guestUser = guestUserRepository.save(guestUser);

                return customTokenService.getGuestToken(guestUser.getId());
            }
        } else {
            GuestUser guestUser = new GuestUser();
            guestUser.setId(guestUserDto.getToken());
            guestUser.setLoginCount(1);
            guestUser.setRole(roleRepository.findByName(Constants.ROLE_GUEST_USER));
            guestUserRepository.save(guestUser);

            return customTokenService.getGuestToken(guestUser.getId());
        }
    }

    private Map<String, Object> createTokenForGuestUser(OAuth2AccessToken auth2AccessToken) {
        Map<String, Object> guestUserToken = new HashMap<>();
        guestUserToken.put("access_token", auth2AccessToken.getValue());
        guestUserToken.put("token_type", auth2AccessToken.getTokenType());
        guestUserToken.put("refresh_token", null);
        guestUserToken.put("expires_in", auth2AccessToken.getExpiresIn());
        guestUserToken.put("scope", auth2AccessToken.getScope());

        return guestUserToken;
    }
}
