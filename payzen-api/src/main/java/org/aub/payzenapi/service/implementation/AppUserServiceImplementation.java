package org.aub.payzenapi.service.implementation;

import lombok.RequiredArgsConstructor;
import org.aub.payzenapi.model.dto.response.AppUserResponse;
import org.aub.payzenapi.model.entity.AppUser;
import org.aub.payzenapi.model.mapper.AppUserMapper;
import org.aub.payzenapi.repository.AuthRepository;
import org.aub.payzenapi.service.AppUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImplementation implements AppUserService {
    private final AuthRepository authRepository;
    private final AppUserMapper appUserMapper;

    public AppUser getAppCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return appUser;
    }

    @Override
    public AppUserResponse getCurrentUser() {
        AppUser appUser = authRepository.findByUserId(getAppCurrentUser().getUserId());
        if (appUser == null) {
            throw new UsernameNotFoundException("Current user not found");
        }
        return appUserMapper.toResponse(appUser);
    }
}