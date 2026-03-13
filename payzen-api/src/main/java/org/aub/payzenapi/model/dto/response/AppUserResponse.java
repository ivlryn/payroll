package org.aub.payzenapi.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}

