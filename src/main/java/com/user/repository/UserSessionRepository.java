package com.user.repository;

import com.user.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    @Query("select us from UserSession us where us.userId =:userId and us.userSessionId =:sessionId and us.signedOutAt is null")
    public UserSession getUserSessionByUserIdAndSessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);

    @Query("SELECT us FROM UserSession us where us.userId=:userId AND us.signedOutAt is not null ORDER BY us.userId DESC LIMIT 1")
    public UserSession getLastSignedDetails(@Param("userId") Long userId);

    UserSession findByUserIdAndSignedOutAtIsNull(Long userId);

}
