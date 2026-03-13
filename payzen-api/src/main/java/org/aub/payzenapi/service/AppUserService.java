package org.aub.payzenapi.service;

import org.aub.payzenapi.model.dto.response.AppUserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
    AppUserResponse getCurrentUser();
}
