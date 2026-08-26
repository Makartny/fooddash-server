package com.kostyan.fooddash.repository;

import com.kostyan.fooddash.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // СЕКРЕТНОЕ ОРУЖИЕ: Спринг сам напишет SQL-запрос SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);
}
