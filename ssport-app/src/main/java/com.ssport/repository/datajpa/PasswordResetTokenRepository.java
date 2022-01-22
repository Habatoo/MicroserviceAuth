package com.ssport.repository.datajpa;

import com.ssport.model.users.PasswordResetToken;
import com.ssport.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUser(User user);
}
