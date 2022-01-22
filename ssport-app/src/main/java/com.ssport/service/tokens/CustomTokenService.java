package com.ssport.service.tokens;

import com.ssport.service.users.CustomUserDetailsService;
import com.ssport.service.users.GuestUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomTokenService {

    private final DefaultTokenServices defaultTokenServices;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final GuestUserDetailsService guestUserDetailsService;
    private final TokenStore tokenStore;
    @Value("${security.oauth2.client.client-id}")
    private String oauth2ClientId;
    @Value("${security.oauth2.client.scope}")
    private String oauth2Scope;

    public CustomTokenService(DefaultTokenServices defaultTokenServices, CustomUserDetailsService customUserDetailsService,
                              AuthenticationManager authenticationManager, GuestUserDetailsService guestUserDetailsService, TokenStore tokenStore) {
        this.defaultTokenServices = defaultTokenServices;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.guestUserDetailsService = guestUserDetailsService;
        this.tokenStore = tokenStore;
    }

    public OAuth2AccessToken getToken(String userName) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
        revokeTokens(userName);

        return defaultTokenServices.createAccessToken(getOAuth2Authentication(userDetails, userDetails.getAuthorities()));
    }

    public OAuth2AccessToken getGuestToken(String tokenId) {

        UserDetails guestUserDetails = guestUserDetailsService.loadUserByUsername(tokenId);

        return defaultTokenServices.createAccessToken(getOAuth2Authentication(guestUserDetails, guestUserDetails.getAuthorities()));
    }

    public boolean revokeTokens(String userName) {
        List<OAuth2AccessToken> tokens = (ArrayList<OAuth2AccessToken>) tokenStore.findTokensByClientIdAndUserName(oauth2ClientId, userName);
        if (tokens.isEmpty()) {
            return true;
        } else {
            OAuth2AccessToken accessToken = tokens.get(0);
            return defaultTokenServices.revokeToken(accessToken.getValue());
        }
    }

    public Authentication getAuthentication(String userName, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
    }

    private OAuth2Authentication getOAuth2Authentication(Object principal, Collection<? extends GrantedAuthority> authorities) {
        Set<String> responseTypes = new HashSet<>();
        responseTypes.add("code");

        OAuth2Request oAuth2Request = new OAuth2Request(new HashMap<>(), oauth2ClientId, authorities,
                true, Collections.singleton(oauth2Scope), new HashSet<>(), null, responseTypes, new HashMap<>());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);

        return new OAuth2Authentication(oAuth2Request, authenticationToken);
    }
}
