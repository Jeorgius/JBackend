package com.jeorgius.jbackend.service;

import com.jeorgius.jbackend.dto.UserDetailsDto;
import com.jeorgius.jbackend.dto.UserDto;
import com.jeorgius.jbackend.dto.UserExtendedDto;
import com.jeorgius.jbackend.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    Page<UserDto> findAll(Pageable pageable, boolean includeDeleted);

    Optional<UserDto> getCurrentUser(boolean exit);

    Page<UserDto> findAllByName(Pageable pageable, String name, boolean archived, Long opaId);

    Optional<UserDto> findById(Long id);

    Optional<UserDto> create(UserDto user);

    void delete(Long id);

    void restore(Long id);

    Optional<UserDto> update(UserDto userDto, Long id);

    boolean isSingularUserWithOpaIdAndRole(Long userId, Role role, Long opaId);

    boolean isDefaultSubjectSet(Long opaId, Role role, Long excludedUserId);

    List<Role> findRoles();

    List<UserDto> findAllByOpaId(Long opaId);

    List<UserDto> findAllByOpaIdAndName(Long opaId, String name);

    Page<UserDto> findAllByOpaIdAndName(Pageable pageable, Long opaId, String name, boolean archived);

    Page<UserDto> findAllByOpaIdsAndNameAndRoles(List<Long> opaIds, String name, List<Role> roles, Pageable pageable);

    List<UserDto> findAllByNameAndRoleAndSubjectId(String name, String role, Long subjectId);

    Optional<List<UserDto>> searchSignersForChangeByName(String name, Long appealId);

    List<UserDto> findAllByNameAndRoleInOpa(String name, Role role);

    List<UserDto> searchByNameAndRoleAndOpa(Long opaId, String name, Role role, List<Long> notInIds, List<Long> subjectIds, List<Long> subsubjectIds);

    Optional<Long> setUserPassword(String verificationCode, String password);


    List<UserDto> findByRoleForCurrentOrganizationPersonalAreaExtended(Role role);

    List<UserDto> findByRoleForOrganizationPersonalAreaIdExtended(Role role, Long opaId, Long subjectId);

    List<UserDto> findByNameAndRoleAndSubjectInOpa(String name, Role role, Long subjectId);

    Page<UserExtendedDto> findByNameAndRoleAndSubjectAndOpaPage(String name, Role role, Long subjectId, Long subSubjectId, Long opaId, Pageable pageable);

    UserDetailsDto findUserDetails(Long id);

    void setTokenStore(TokenStore tokenStore);

    void sendRegistrationEmail(Long id);

    void importUsersFromXlsx(MultipartFile file);

}
