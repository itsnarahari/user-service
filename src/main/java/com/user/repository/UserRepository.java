package com.user.repository;

import com.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String userName);

    @Query("select u from User u where u.username = :userName and u.verified=true")
    User checkIfUserExistAndIsVerified(@Param(value = "userName") String userName);

    User findUserByUsername(String username);

}
