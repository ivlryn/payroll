package org.aub.payzenapi.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
        @NotBlank
        @Pattern(regexp = "^[\\w\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email")
        @Schema(description = "email of the user", example = "example@gmail.com", defaultValue = "example@gmail.com")
        private String email;

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one special character (@#$%^&+=!).")
        @Schema(description = "password", example = "password", defaultValue = "password")
        private String password;
}

