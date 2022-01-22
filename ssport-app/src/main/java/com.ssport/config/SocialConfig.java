package com.ssport.config;

import com.ssport.service.social.SocialConnectionSignUp;
import com.ssport.service.social.SocialSignInAdapter;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;

import javax.annotation.Resource;

@Configuration
public class SocialConfig {

    @Value("${spring.social.facebook.appId}")
    private String facebookAppId;

    @Value("${spring.social.facebook.appSecret}")
    private String facebookAppSecret;

    @Value("${spring.social.google.appId}")
    private String googleAppId;

    @Value("${spring.social.google.appSecret}")
    private String googleAppSecret;

    @Resource
    private SocialConnectionSignUp socialConnectionSignUp;

    @Resource
    private SocialSignInAdapter socialSignInAdapter;

    @Resource
    private HikariDataSource dataSource;

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        FacebookConnectionFactory fcf = new FacebookConnectionFactory(facebookAppId, facebookAppSecret);
        fcf.setScope("public_profile,email");

        GoogleConnectionFactory gcf = new GoogleConnectionFactory(googleAppId, googleAppSecret);
        gcf.setScope("profile email");

        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(fcf);
        registry.addConnectionFactory(gcf);

        return registry;
    }

    @Bean
    public UsersConnectionRepository usersConnectionRepository() {
        JdbcUsersConnectionRepository connectionRepository = new JdbcUsersConnectionRepository(dataSource,
                connectionFactoryLocator(), Encryptors.noOpText());
        connectionRepository.setConnectionSignUp(socialConnectionSignUp);

        return connectionRepository;
    }

    @Bean
    public ProviderSignInController providerSignInController() {

        return new ProviderSignInController(connectionFactoryLocator(), usersConnectionRepository(), socialSignInAdapter);
    }
}
