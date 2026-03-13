package org.aub.payzenapi.repository;

import org.aub.payzenapi.model.dto.response.UserResponse;
import org.aub.payzenapi.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    @Query("SELECT new org.aub.payzenapi.model.dto.response.UserResponse(" +
           "a.firstName, a.lastName, a.email) " +
           "FROM AppUser a WHERE a.userId = :id")
    UserResponse getUserResponseById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE AppUser a SET a.isVerified = true WHERE a.email = :email")
    void updateVerificationStatus(@Param("email") String email);
}
