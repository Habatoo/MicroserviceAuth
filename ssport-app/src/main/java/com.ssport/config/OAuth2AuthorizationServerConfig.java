package com.ssport.config;

import com.ssport.exception.CustomClientAuthenticationException;
import com.ssport.exception.CustomOauthException;
import com.ssport.exception.CustomUsernameNotFoundException;
import com.ssport.exception.ErrorCode;
import com.ssport.service.users.CustomUserDetailsService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${security.oauth2.client.client-id}")
    private String oauth2ClientId;

    @Value("${security.oauth2.client.client-secret}")
    private String oauth2ClientSecret;

    @Value("${security.oauth2.client.scope}")
    private String oauth2Scope;

    @Value("${security.oauth2.resource.id}")
    private String oauth2ResourceId;

    @Value("${security.oauth2.client.access-token-validity-seconds}")
    private int accessTokenValiditySeconds;

    @Value("${security.oauth2.client.refresh-token-validity-seconds}")
    private int refreshTokenValiditySeconds;

    @Resource
    private HikariDataSource dataSource;

    @Resource
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
        return new OAuth2AccessDeniedHandler();
    }


    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices() {
            @Override
            public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
                OAuth2AccessToken accessToken = tokenStore().readAccessToken(accessTokenValue);
                if (accessToken == null) {
                    throw new CustomClientAuthenticationException("Invalid access token: " + accessTokenValue, ErrorCode.INVALID_ACCESS_TOKEN);
                } else if (accessToken.isExpired()) {
                    tokenStore().removeAccessToken(accessToken);
                    throw new CustomClientAuthenticationException("Access token expired: " + accessTokenValue, ErrorCode.ACCESS_TOKEN_EXPIRED);
                } else {
                    OAuth2Authentication result = tokenStore().readAuthentication(accessToken);
                    if (result == null) {
                        throw new CustomClientAuthenticationException("Invalid access token: " + accessTokenValue, ErrorCode.INVALID_ACCESS_TOKEN);
                    } else {
                        if (jdbcClientDetailsService() != null) {
                            String clientId = result.getOAuth2Request().getClientId();

                            try {
                                jdbcClientDetailsService().loadClientByClientId(clientId);
                            } catch (ClientRegistrationException var6) {
                                throw new CustomClientAuthenticationException("Client not valid: " + clientId, var6, ErrorCode.CLIENT_NOT_VALID);
                            }
                        }

                        return result;
                    }
                }
            }
        };
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setClientDetailsService(jdbcClientDetailsService());

        return defaultTokenServices;
    }

    @Bean
    public JdbcClientDetailsService jdbcClientDetailsService() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId(oauth2ClientId);
        clientDetails.setClientSecret(passwordEncoder.encode(oauth2ClientSecret));
        clientDetails.setAuthorizedGrantTypes(Arrays.asList("password", "refresh_token"));
        clientDetails.setScope(Collections.singletonList(oauth2Scope));
        clientDetails.setResourceIds(Collections.singletonList(oauth2ResourceId));
        clientDetails.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        clientDetails.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);

        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        try {
            jdbcClientDetailsService.updateClientDetails(clientDetails);
        } catch (NoSuchClientException e) {
            jdbcClientDetailsService.addClientDetails(clientDetails);
        }

        return jdbcClientDetailsService;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(passwordEncoder)
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(jdbcClientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
                .userDetailsService(customUserDetailsService)
                .reuseRefreshTokens(false);
        endpoints.exceptionTranslator(exception -> {
            if (exception instanceof CustomOauthException) {
                CustomOauthException customOauthException = (CustomOauthException) exception;
                return ResponseEntity
                        .status(customOauthException.getHttpErrorCode())
                        .body(customOauthException);
            } else if (exception instanceof CustomUsernameNotFoundException) {
                CustomUsernameNotFoundException customUsernameNotFoundException = (CustomUsernameNotFoundException) exception;
                return ResponseEntity
                        .status(customUsernameNotFoundException.getCode().getHttpStatus())
                        .body(new CustomOauthException(customUsernameNotFoundException.getMessage(), customUsernameNotFoundException.getCode()));
            } else if (exception instanceof InvalidGrantException) {
                CustomUsernameNotFoundException customUsernameNotFoundException = new CustomUsernameNotFoundException("Invalid username or password.", ErrorCode.USERNAME_OR_PASSWORD_ERROR);
                return ResponseEntity
                        .status(customUsernameNotFoundException.getCode().getHttpStatus())
                        .body(new CustomOauthException(customUsernameNotFoundException.getMessage(), customUsernameNotFoundException.getCode()));
            } else {
                throw exception;
            }
        });
    }
}