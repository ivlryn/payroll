package org.aub.payzenapi.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRequest {
    @NotBlank
    @Pattern(regexp = "^\\w+$", message = "No spacing allow")
    @Schema(defaultValue = "firstname")
    private String firstName;   

    @NotBlank
    @Pattern(regexp = "^\\w+$", message = "No spacing allow")
    @Schema(defaultValue = "lastname")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[\\w\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email")
    @Schema(description = "Full name of the user", example = "example@gmail.com", defaultValue = "example@gmail.com")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one special character (@#$%^&+=!).")
    private String password;
}
