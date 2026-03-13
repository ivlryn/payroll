package org.aub.payzenapi.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aub.payzenapi.exception.BadRequestException;
import org.aub.payzenapi.exception.NotFoundException;
import org.aub.payzenapi.jwt.JwtService;
import org.aub.payzenapi.model.dto.request.AppUserRequest;
import org.aub.payzenapi.model.dto.request.AuthRequest;
import org.aub.payzenapi.model.dto.request.PasswordRequest;
import org.aub.payzenapi.model.dto.response.AppUserResponse;
import org.aub.payzenapi.model.dto.response.AuthResponse;
import org.aub.payzenapi.model.entity.AppUser;
import org.aub.payzenapi.model.mapper.AppUserMapper;
import org.aub.payzenapi.repository.AppUserRepository;
import org.aub.payzenapi.repository.AuthRepository;
import org.aub.payzenapi.service.AppUserService;
import org.aub.payzenapi.service.AuthService;
import org.aub.payzenapi.service.EmailSenderService;
import org.aub.payzenapi.service.OtpService;
import org.aub.payzenapi.utils.RandomOtp;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {
    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;
    private final EmailSenderService emailSenderService;
    //    private final RedisTemplate<String, String> redisTemplate;
    private final OtpService otpService;

    public AppUser getAppCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void authenticate(String email, String password) {
        try {
            AppUser appUser = authRepository.getUserByEmail(email);
            if (appUser == null) {
                throw new NotFoundException("Invalid email");
            }
            if (!passwordEncoder.matches(password, appUser.getPassword())) {
                throw new NotFoundException("Invalid Password");
            }
            if (!appUser.getIsVerified()) {
                throw new BadRequestException("Your account is not verified");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new BadRequestException("USER_DISABLED: " + e.getMessage());
        } catch (BadCredentialsException e) {
            throw new BadRequestException("INVALID_CREDENTIALS: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        String email = authRequest.getEmail().toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new BadRequestException("User is not registered");
        if (!appUser.getIsVerified()) throw new BadRequestException("User needs to verify before login");
        authenticate(email, authRequest.getPassword());
        final UserDetails userDetails = appUserService.loadUserByUsername(email);
        final String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    @SneakyThrows
    @Override
    public AppUserResponse register(AppUserRequest appUserRequest) {
        if (authRepository.getUserByEmail(appUserRequest.getEmail().toLowerCase()) != null)
            throw new BadRequestException("User already exists");

        AppUser appUser = new AppUser();
        appUser.setFirstName(appUserRequest.getFirstName());
        appUser.setLastName(appUserRequest.getLastName());
        appUser.setEmail(appUserRequest.getEmail().toLowerCase());
        appUser.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));
        appUser.setIsVerified(false);
        appUser.setCreatedAt(LocalDateTime.now());

        AppUser savedUser = authRepository.save(appUser);
        String otp = new RandomOtp().generateOtp();
        otpService.storeOtp(savedUser.getEmail(), otp);
        emailSenderService.sendEmail(savedUser.getEmail(), otp);
        return appUserMapper.toResponse(savedUser);
    }

    @Override
    public void verify(String emailRequest, String otpCode) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");
        if (appUser.getIsVerified()) throw new BadRequestException("User already verified");

        String storedOTP = otpService.getOtp(email);
        if (storedOTP == null) throw new BadRequestException("OTP already expired");
        if (!storedOTP.equals(otpCode)) throw new BadRequestException("OTP code doesn't match");

        otpService.removeOtp(email);
        appUserRepository.updateVerificationStatus(email);
    }

    @SneakyThrows
    @Override
    public void resend(String emailRequest) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");
        if (appUser.getIsVerified()) throw new BadRequestException("User already verified");
        String otp = new RandomOtp().generateOtp();

        otpService.storeOtp(email, otp);
        emailSenderService.sendEmail(appUser.getEmail(), otp);
    }

    @Override
    public void forgotPassword(String emailRequest) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");
        String otp = new RandomOtp().generateOtp();

        otpService.storeOtp(email, otp);
        emailSenderService.sendEmail(appUser.getEmail(), otp);
    }

    @Override
    public void verifyForgot(String emailRequest, String otpCode) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");

        String storedOTP = otpService.getOtp(email);
        if (storedOTP == null) throw new BadRequestException("OTP already expired");
        if (!storedOTP.equals(otpCode)) throw new BadRequestException("OTP code doesn't match");
    }

    @Override
    public AppUserResponse resetPassword(String emailRequest, String otpCode, String newPassword) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");

        String storedOTP = otpService.getOtp(email);
        if (storedOTP == null) throw new BadRequestException("OTP already expired");
        if (!storedOTP.equals(otpCode)) throw new BadRequestException("OTP code doesn't match");

        otpService.removeOtp(email);

        String encodedPassword = passwordEncoder.encode(newPassword);
        authRepository.updatePassword(email, encodedPassword);
        AppUser updatedUser = authRepository.getUserByEmail(email);

        return appUserMapper.toResponse(updatedUser);
    }
    @Override
    public void verifyOldPassword(PasswordRequest passwordRequest) {
        if (!passwordEncoder.matches(passwordRequest.getPassword(), getAppCurrentUser().getPassword()))
            throw new BadRequestException("Wrong password");
    }

    @Override
    public void changePassword(PasswordRequest passwordRequest) {
        String encodedPassword = passwordEncoder.encode(passwordRequest.getPassword());
        authRepository.changePassword(encodedPassword, getAppCurrentUser().getUserId());
    }
}