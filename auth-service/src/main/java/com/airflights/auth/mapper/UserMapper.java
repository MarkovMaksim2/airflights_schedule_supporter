package com.airflights.auth.mapper;

import com.airflights.auth.dto.CreateUserRequestDto;
import com.airflights.auth.dto.UpdateUserRolesRequestDto;
import com.airflights.auth.dto.UserDto;
import com.airflights.auth.entity.AppUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(AppUser appUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // пароль выставим в сервисе
    @Mapping(target = "enabled", expression = "java(Boolean.TRUE.equals(src.getEnabled()))")
    AppUser toEntity(CreateUserRequestDto src);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    void updateRoles(UpdateUserRolesRequestDto src, @MappingTarget AppUser target);

}
