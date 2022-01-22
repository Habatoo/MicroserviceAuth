package com.ssport.service.users;

import com.ssport.exception.CustomUsernameNotFoundException;
import com.ssport.exception.ErrorCode;
import com.ssport.model.users.Role;
import com.ssport.model.users.User;
import com.ssport.model.users.UserPrincipal;
import com.ssport.repository.datajpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new CustomUsernameNotFoundException("Invalid username or password.", ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        if (!user.isEmailVerified()) {
            return new UserPrincipal(user, false, true, true, true, getAuthorities(user.getRoles()));
        }

        return new UserPrincipal(user, getAuthorities(user.getRoles()));
    }

    public UserDetails loadUserByAppleId(String s) throws UsernameNotFoundException {
        User user = userRepository.findByAppleId(s).orElse(null);
        if (user == null) {
            throw new CustomUsernameNotFoundException("Invalid apple id.", ErrorCode.USER_NOT_FOUND);
        }

        if (!user.isEmailVerified()) {
            return new UserPrincipal(user, false, true, true, true, getAuthorities(user.getRoles()));
        }

        return new UserPrincipal(user, getAuthorities(user.getRoles()));
    }

    private List<GrantedAuthority> getAuthorities(Set<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        }
        return authorities;
    }
}
