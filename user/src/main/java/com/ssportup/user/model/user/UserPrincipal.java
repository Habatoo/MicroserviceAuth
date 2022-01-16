package com.ssportup.user.model.user;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPrincipal extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = -6948442527291086054L;

    private final User user;

    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);
        this.user = user;
    }

    public UserPrincipal(User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                         boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                authorities);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
