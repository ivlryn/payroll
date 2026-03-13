package org.aub.payzenapi.service;

import jakarta.mail.MessagingException;
import org.aub.payzenapi.model.dto.request.AppUserRequest;
import org.aub.payzenapi.model.dto.request.AuthRequest;
import org.aub.payzenapi.model.dto.request.PasswordRequest;
import org.aub.payzenapi.model.dto.response.AppUserResponse;
import org.aub.payzenapi.model.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest) throws Exception;

    AppUserResponse register(AppUserRequest appUserRequest) throws MessagingException;

    void verify(String email, String optCode);

    void resend(String email) throws MessagingException;

    void forgotPassword(String email);

    void verifyForgot(String email, String otpCode);

    AppUserResponse resetPassword(String email, String otpCode, String newPassword);

    void verifyOldPassword(PasswordRequest passwordRequest);

    void changePassword(PasswordRequest passwordRequest);
}
