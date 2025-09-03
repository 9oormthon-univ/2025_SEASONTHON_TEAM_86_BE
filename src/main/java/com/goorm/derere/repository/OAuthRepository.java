package com.goorm.derere.repository;
import org.springframework.stereotype.Repository;
import com.goorm.derere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface OAuthRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserid(Long userid);
    Optional<User> findUserByEmail(String email);
}

