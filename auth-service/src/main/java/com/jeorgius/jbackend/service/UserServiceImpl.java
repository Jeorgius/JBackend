package com.jeorgius.jbackend.service;

import com.jeorgius.jbackend.dto.UserDetailsDto;
import com.jeorgius.jbackend.dto.UserDto;
import com.jeorgius.jbackend.dto.UserExtendedDto;
import com.jeorgius.jbackend.entities.User;
import com.jeorgius.jbackend.entities.UserPerRole;
import com.jeorgius.jbackend.enums.Role;
import com.jeorgius.jbackend.enums.UserStatus;
import com.jeorgius.jbackend.mapper.UserDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDetailsMapper userDetailsMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserDto> findAll(Pageable pageable, boolean includeDeleted) {
        // ToDo use real users
        return new PageImpl<>(List.of(
                new UserDto(
                    "email@somedomain.com",
                    "Jeorgius",
                    "J",
                    UserStatus.CONFIRMED,
                    false,
                    true)
        ));
    }

    @Override
    public Optional<UserDto> getCurrentUser(boolean exit) {
        return Optional.empty();
    }

    @Override
    public Page<UserDto> findAllByName(Pageable pageable, String name, boolean archived, Long opaId) {
        return null;
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        // ToDo use real users
        if (id != null && id.equals(1L)) {
            var user = new UserDto(
                    "email@somedomain.com",
                    "Jeorgius",
                    "J",
                    UserStatus.CONFIRMED,
                    false,
                    true);
            user.setId(1L);
            return Optional.of(user);
        } else
            return Optional.empty();
    }

    @Override
    public Optional<UserDto> create(UserDto user) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void restore(Long id) {

    }

    @Override
    public Optional<UserDto> update(UserDto userDto, Long id) {
        return Optional.empty();
    }

    @Override
    public boolean isSingularUserWithOpaIdAndRole(Long userId, Role role, Long opaId) {
        return false;
    }

    @Override
    public boolean isDefaultSubjectSet(Long opaId, Role role, Long excludedUserId) {
        return false;
    }

    @Override
    public List<Role> findRoles() {
        return null;
    }

    @Override
    public List<UserDto> findAllByOpaId(Long opaId) {
        return null;
    }

    @Override
    public List<UserDto> findAllByOpaIdAndName(Long opaId, String name) {
        return null;
    }

    @Override
    public Page<UserDto> findAllByOpaIdAndName(Pageable pageable, Long opaId, String name, boolean archived) {
        return null;
    }

    @Override
    public Page<UserDto> findAllByOpaIdsAndNameAndRoles(List<Long> opaIds, String name, List<Role> roles, Pageable pageable) {
        return null;
    }

    @Override
    public List<UserDto> findAllByNameAndRoleAndSubjectId(String name, String role, Long subjectId) {
        return null;
    }

    @Override
    public Optional<List<UserDto>> searchSignersForChangeByName(String name, Long appealId) {
        return Optional.empty();
    }

    @Override
    public List<UserDto> findAllByNameAndRoleInOpa(String name, Role role) {
        return null;
    }

    @Override
    public List<UserDto> searchByNameAndRoleAndOpa(Long opaId, String name, Role role, List<Long> notInIds, List<Long> subjectIds, List<Long> subsubjectIds) {
        return null;
    }

    @Override
    public Optional<Long> setUserPassword(String verificationCode, String password) {
        return Optional.empty();
    }

    @Override
    public List<UserDto> findByRoleForCurrentOrganizationPersonalAreaExtended(Role role) {
        return null;
    }

    @Override
    public List<UserDto> findByRoleForOrganizationPersonalAreaIdExtended(Role role, Long opaId, Long subjectId) {
        return null;
    }

    @Override
    public List<UserDto> findByNameAndRoleAndSubjectInOpa(String name, Role role, Long subjectId) {
        return null;
    }

    @Override
    public Page<UserExtendedDto> findByNameAndRoleAndSubjectAndOpaPage(String name, Role role, Long subjectId, Long subSubjectId, Long opaId, Pageable pageable) {
        return null;
    }

    @Override
    public UserDetailsDto findUserDetails(Long id) {
        // ToDo use real users
        var user = new User();
        user.setEmail("email@somedomain.com");
        user.setId(1L);
        user.setUserStatus(UserStatus.CONFIRMED);
        user.setName("J");
        user.setSurname("Jeorgius");
        user.setSystem(true);
        user.setPassword(passwordEncoder.encode("123"));
        return Optional.of(user)
                .map(userDetailsMapper::convert)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %d not authenticated", id)));
    }

    @Override
    public void setTokenStore(TokenStore tokenStore) {

    }


    @Override
    public void sendRegistrationEmail(Long id) {

    }

    @Override
    public void importUsersFromXlsx(MultipartFile file) {

    }

    //ToDo use real users
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = new User();
        user.setEmail("email@somedomain.com");
        user.setId(1L);
        user.setUserStatus(UserStatus.CONFIRMED);
        user.setName("J");
        user.setSurname("Jeorgius");
        user.setSystem(true);
        user.setPassword(passwordEncoder.encode("123"));
        var role = new UserPerRole();
        role.setRole(Role.SUPERUSER);
        role.setUser(user);
        role.setId(1L);
        role.setSuperuser(true);
        user.setUserPerRoles(List.of(role));
        return Optional.of(user).filter(x -> x.getUserStatus() == UserStatus.CONFIRMED && x.getPassword() != null)
                .map(userDetailsMapper::convert)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not authenticated", username)));
    }
}
