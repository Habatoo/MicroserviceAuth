package com.ssport.service.social;

import com.ssport.model.users.SocialMediaService;
import com.ssport.model.users.User;
import com.ssport.repository.datajpa.RoleRepository;
import com.ssport.repository.datajpa.UserRepository;
import com.ssport.util.Constants;
import com.ssport.util.DateTimeUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SocialConnectionSignUp implements ConnectionSignUp {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public SocialConnectionSignUp(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String execute(Connection<?> connection) {
        String providerId = connection.getKey().getProviderId();

        String email;
        String fullName;

        String avatarLink = connection.getImageUrl();
        SocialMediaService socialMediaService = SocialMediaService.valueOf(providerId.toUpperCase());

        if (providerId.equals("facebook")) {
            Facebook facebook = (Facebook) connection.getApi();
            org.springframework.social.facebook.api.User facebookUser = facebook.fetchObject("me",
                    org.springframework.social.facebook.api.User.class, Constants.facebookFieldsForSignUp);
            email = facebookUser.getEmail();
            fullName = facebookUser.getFirstName() + " " + facebookUser.getLastName();
        } else {
            UserProfile userProfile = connection.fetchUserProfile();
            email = userProfile.getEmail();
            fullName = userProfile.getFirstName() + " " + userProfile.getLastName();
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setAvatarLink(avatarLink);
        user.getRoles().add(roleRepository.findByName(Constants.ROLE_SOCIAL_USER));
        user.setDateJoined(DateTimeUtil.getLocalDateTimeUtc());
        user.setEmailVerified(true);
        user.setSignInProvider(socialMediaService);

        user = userRepository.save(user);
        return user.getEmail();
    }
}
