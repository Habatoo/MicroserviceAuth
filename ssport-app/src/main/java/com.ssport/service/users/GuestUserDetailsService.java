package com.ssport.service.users;

import com.ssport.exception.CustomUsernameNotFoundException;
import com.ssport.exception.ErrorCode;
import com.ssport.model.users.GuestUser;
import com.ssport.repository.datajpa.GuestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GuestUserDetailsService implements UserDetailsService {

    private final GuestUserRepository guestUserRepository;

    @Autowired
    public GuestUserDetailsService(GuestUserRepository guestUserRepository) {
        this.guestUserRepository = guestUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String guestUserToken) throws UsernameNotFoundException {
        GuestUser guestUser = guestUserRepository.findById(guestUserToken).orElse(null);
        if (guestUser == null) {
            throw new CustomUsernameNotFoundException("User not found.", ErrorCode.USER_NOT_FOUND);
        }

        return new User(guestUser.getId(), "", Collections.singletonList(new SimpleGrantedAuthority(guestUser.getRole().getName())));
    }
}
