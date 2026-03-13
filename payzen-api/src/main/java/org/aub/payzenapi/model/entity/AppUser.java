package org.aub.payzenapi.model.entity;

import lombok.*;
import org.aub.payzenapi.model.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "app_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "is_verified")
    private Boolean isVerified = false;  // Default to false

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;


    @Version
    private Long version;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isVerified == null) {
            isVerified = false;
        }
        if (role == null) {
            role = UserRole.USER;
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified != null && isVerified;
    }
}