package com.jeorgius.jbackend.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.jeorgius.jbackend.dto.UserPerRoleDto;
import com.jeorgius.jbackend.entities.UserPerRole;
import com.jeorgius.jbackend.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Component
@AllArgsConstructor
public class UserPerRoleMapper implements BiDirectMapper<UserPerRole, UserPerRoleDto> {
    private UserUtils userUtils;

    @Override
    public UserPerRole revert(UserPerRoleDto dto, Supplier<UserPerRole> factory) {
        UserPerRole entity = factory.get();
        entity.setRole(dto.getRole());
        entity.setSuperuser(dto.isSuperuser());
        return entity;
    }

    @Override
    public UserPerRole revert(UserPerRoleDto target) {
        return revert(target, UserPerRole::new);
    }

    @Override
    public UserPerRoleDto convert(UserPerRole entity, Supplier<UserPerRoleDto> factory) {
        UserPerRoleDto dto = factory.get();
        dto.setId(entity.getId());
        dto.setRole(entity.getRole());
        dto.setSuperuser(entity.isSuperuser());

        return dto;
    }

    @Override
    public UserPerRoleDto convert(UserPerRole entity) {
        return convert(entity, UserPerRoleDto::new);
    }
}
