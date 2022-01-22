package com.ssport.model.users;

import javax.persistence.*;
import java.io.Serializable;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
