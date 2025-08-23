package com.example.Backend.services;

import com.example.Backend.dtos.user.UserRequest;
import com.example.Backend.dtos.user.UserResponse;
import com.example.Backend.events.PasswordResetRequestedEvent;
import com.example.Backend.events.UserRegisteredEvent;
import com.example.Backend.exceptions.UserException;
import com.example.Backend.mappers.UserMapper;
import com.example.Backend.models.Role;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.RoleRepository;
import com.example.Backend.repositorys.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper,
                      RoleRepository roleRepository, ApplicationEventPublisher eventPublisher,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest request) {
        validateUserRequest(request);

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        // Assign default role if not specified
        Set<Role> roles = new HashSet<>();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new UserException("Role not found: " + roleId)))
                    .collect(Collectors.toSet());
        } else {
            // Assign default SALES role
            Role defaultRole = roleRepository.findByCode("SALES")
                    .orElseThrow(() -> new UserException("Default role SALES not found"));
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Publish event
//        eventPublisher.publishEvent(new UserRegisteredEvent(savedUser));

        return userMapper.toResponse(savedUser);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserById(id);

        // Update basic info
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // Update roles if provided
        if (request.getRoleIds() != null) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new UserException("Role not found: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public void updateLastLoginTime(String username) {
        userRepository.updateLastLoginTime(username, LocalDateTime.now());
    }

    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        return userRepository.findActiveUsersWithSearch(search, pageable)
                .map(userMapper::toResponse);
    }

    public List<UserResponse> getUsersByRole(String roleCode) {
        return userRepository.findByRoleCodeAndIsActiveTrue(roleCode).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deactivateUser(Long id) {
        User user = findUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = findUserById(id);
        user.setIsActive(true);
        userRepository.save(user);
    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found with email: " + email));

//        eventPublisher.publishEvent(new PasswordResetRequestedEvent(user));
    }

    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }

    public List<UserResponse> getRecentlyActiveUsers(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userRepository.findUsersLoggedInSince(since).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with id: " + id));
    }

    private void validateUserRequest(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserException("Username already exists: " + request.getUsername());
        }

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email already exists: " + request.getEmail());
        }
    }

    public Page<UserResponse> getUsersPaginated(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toResponse);
    }

    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserException("User not found with username: " + username));
    }

    public List<UserResponse> getActiveUsers() {
        return userRepository.findByIsActiveTrue().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> searchUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    public long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserResponse> getInactiveUsers() {
        return userRepository.findByIsActiveFalse().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
