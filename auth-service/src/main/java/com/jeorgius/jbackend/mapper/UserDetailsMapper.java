package com.jeorgius.jbackend.mapper;

import com.jeorgius.jbackend.dto.UserDetailsDto;
import com.jeorgius.jbackend.entities.User;
import com.jeorgius.jbackend.entities.UserPerRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserDetailsMapper implements Mapper<User, UserDetailsDto> {
    private UserPerRoleMapper userPerRoleMapper;

    @Override
    public UserDetailsDto convert(User src, Supplier<UserDetailsDto> factory) {
        if (src == null) {
            return null;
        }
        var dto = factory.get();
        dto.setId(src.getId());
        dto.setUsername(src.getEmail());
        dto.setPassword(src.getPassword());
        Optional.ofNullable(src.getUserPerRoles())
                .ifPresent(x -> dto.getAuthorities().addAll(x.stream().map(UserPerRole::getRole).collect(Collectors.toSet())));
        Optional.ofNullable(src.getUserPerRoles()).ifPresent(x -> dto.getRoleDtos().addAll(userPerRoleMapper.convert(x)));
        return dto;
    }

    @Override
    public UserDetailsDto convert(User src) {
        return convert(src, UserDetailsDto::new);
    }
}
