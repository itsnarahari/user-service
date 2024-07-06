package com.user.repository;

import com.user.entities.SignupRequest;
import com.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignupRequestRepository extends JpaRepository<SignupRequest, Long> {

    Optional<SignupRequest> findByUsername(String userName);

}
