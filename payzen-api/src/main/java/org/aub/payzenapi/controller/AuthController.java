package org.aub.payzenapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aub.payzenapi.base.ApiResponse;
import org.aub.payzenapi.base.BaseController;
import org.aub.payzenapi.model.dto.request.AppUserRequest;
import org.aub.payzenapi.model.dto.request.AuthRequest;
import org.aub.payzenapi.model.dto.request.ResetRequest;
import org.aub.payzenapi.model.dto.response.AppUserResponse;
import org.aub.payzenapi.model.dto.response.AuthResponse;
import org.aub.payzenapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auths")
public class AuthController extends BaseController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) throws Exception {
        return response("Login successfully", authService.login(request));
    }

    @SneakyThrows
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AppUserResponse>> register(@Valid @RequestBody AppUserRequest request){
        return response("Register successfully", HttpStatus.CREATED, authService.register(request));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify email with OTP")
    public ResponseEntity<ApiResponse<Object>> verify(@Email @RequestParam String email, @RequestParam @Positive(message = "Otp code cannot be negative or zero") String otpCode) {
        authService.verify(email, otpCode);
        return response("Verified successfully!!!");
    }

    @SneakyThrows
    @PostMapping("/resend")
    @Operation(summary = "Resent verification OTP")
    public ResponseEntity<ApiResponse<Object>> resend(@Email @RequestParam String email) {
        authService.resend(email);
        return response("OTP has successfully resent");
    }

    @PostMapping("/forgot")
    @Operation(summary = "Send otp code to email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@Email @RequestParam String email) {
        authService.forgotPassword(email);
        return response("Email verified");
    }

    @PostMapping("/forgot/verify")
    @Operation(summary = "Verify otp in forgot password")
    public ResponseEntity<ApiResponse<Object>> verifyForgot(@Email @RequestParam String email, @RequestParam String otp) {
        authService.verifyForgot(email, otp);
        return response("Verify forgot password");
    }

    @PostMapping("/forgot/reset")
    @Operation(summary = "Reset password otp in forgot password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetRequest request) {
        return response("successful", authService.resetPassword(request.getEmail(), request.getOtp(), request.getPassword()));
    }

}
