package com.jeorgius.jbackend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDetailsDto implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private List<GrantedAuthority> authorities = new ArrayList<>();
    private List<UserPerRoleDto> roleDtos = new ArrayList<>();
    private Long superUserId;
}
