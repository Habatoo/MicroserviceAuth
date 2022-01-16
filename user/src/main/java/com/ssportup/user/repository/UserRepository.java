package com.ssportup.user.repository;

import com.ssportup.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий с реализацией методов CRUD сущности {@link User}.
 *
 * @author habatoo
 * @version 0.001
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserId(Long userId);
    Optional<User> findByUserEmail(String userEmail);

    Boolean existsByUserName(String userName);
    Boolean existsByUserEmail(String userEmail);
}
