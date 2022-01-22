package com.ssport.repository.datajpa;

import com.ssport.model.users.GuestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestUserRepository extends JpaRepository<GuestUser, String> {
}
