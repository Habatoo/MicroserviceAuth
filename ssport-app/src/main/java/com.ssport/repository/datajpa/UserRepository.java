package com.ssport.repository.datajpa;

import com.ssport.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsEmailVerifiedIsTrue(String email);

    Optional<User> findByEmailAndIsEmailVerifiedIsFalse(String email);

    Optional<User> findByIdAndEmailAndIsEmailVerifiedIsFalse(UUID uuid, String email);

    Optional<User> findByIdAndIsEmailVerified(UUID id, boolean isEmailVerified);

    Optional<User> findByAppleId(String appleId);

}
