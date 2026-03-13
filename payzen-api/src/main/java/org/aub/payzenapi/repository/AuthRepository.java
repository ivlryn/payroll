package org.aub.payzenapi.repository;

import org.aub.payzenapi.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<AppUser, UUID> {

    @Query("SELECT a FROM AppUser a WHERE a.email = :email")
    AppUser getUserByEmail(@Param("email") String email);


    AppUser findByUserId(UUID userId);

    @Modifying
    @Query("UPDATE AppUser a SET a.password = :password WHERE a.email = :email")
    int updatePassword(@Param("email") String email, @Param("password") String password);

    @Modifying
    @Query("UPDATE AppUser a SET a.password = :password WHERE a.userId = :appUserId")
    void changePassword(@Param("password") String password, @Param("appUserId") UUID appUserId);
}