package com.app.ecom.mapper;

import com.app.ecom.dto.userDtos.UserRequest;
import com.app.ecom.dto.userDtos.UserResponse;
import com.app.ecom.entity.User;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Convert UserRequest to User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "address", ignore = true)
    User toEntity(UserRequest userRequest);

    /**
     * Convert User entity to UserResponse
     */
    @Mapping(source = "userRole", target = "role")
    UserResponse toResponse(User user);

    /**
     * Update existing user with non-null values from request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "address", ignore = true)
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget User user);
}