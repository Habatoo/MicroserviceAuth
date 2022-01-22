package com.ssport.service.users;

import com.ssport.exception.CustomOauthException;
import com.ssport.exception.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsChecker implements UserDetailsChecker {
    @Override
    public void check(UserDetails userDetails) {
        if (!userDetails.isEnabled()) {
            throw new CustomOauthException("Email is not verified", ErrorCode.EMAIL_IS_NOT_VERIFIED);
        }
    }
}
