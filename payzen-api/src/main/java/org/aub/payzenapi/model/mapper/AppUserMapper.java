package org.aub.payzenapi.model.mapper;

import org.aub.payzenapi.model.entity.AppUser;
import org.aub.payzenapi.model.dto.response.AppUserResponse;
import org.aub.payzenapi.model.dto.response.UserResponse;
import org.aub.payzenapi.model.dto.request.AppUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isVerified", constant = "false")
    AppUser toEntity(AppUserRequest request);

    AppUserResponse toResponse(AppUser appUser);
    UserResponse toUserResponse(AppUserResponse appUserResponse);
}