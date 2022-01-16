package com.ssportup.user.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "guest_users")
public class GuestUser implements Serializable {

    private static final long serialVersionUID = 1154681902469625674L;

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "login_count")
    private int loginCount;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
