package com.ssport.service.social;

import com.ssport.util.Constants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Collections;

@Service
public class SocialSignInAdapter implements SignInAdapter {

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(connection.getDisplayName(), null,
                        Collections.singletonList(new SimpleGrantedAuthority(Constants.ROLE_SOCIAL_USER))));

        return null;
    }
}

