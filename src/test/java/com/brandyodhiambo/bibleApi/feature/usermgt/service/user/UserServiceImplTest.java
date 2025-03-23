package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.Role;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.RoleName;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.RoleRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private Users testUser;
    private SignUpRequestDto signUpRequestDto;
    private LoginRequestDto loginRequestDto;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new Users();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setEmailVerified(true);
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        Set<Role> roles = new HashSet<>();
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.ROLE_MEMBER);
        roles.add(userRole);
        testUser.setRole(roles);

        // Setup signup request
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_MEMBER");
        signUpRequestDto = new SignUpRequestDto("New", "User", "newuser", "newuser@example.com", "password123", roleSet);

        // Setup login request
        loginRequestDto = new LoginRequestDto("testuser", "password123");
    }

    @Test
    void checkUsernameAvailability_WhenUsernameExists_ReturnsFalse() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        Boolean result = userService.checkUsernameAvailability("testuser");

        // Assert
        assertFalse(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void checkUsernameAvailability_WhenUsernameDoesNotExist_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        Boolean result = userService.checkUsernameAvailability("nonexistent");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    void checkEmailAvailability_WhenEmailExists_ReturnsFalse() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        Boolean result = userService.checkEmailAvailability("test@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void checkEmailAvailability_WhenEmailDoesNotExist_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        Boolean result = userService.checkEmailAvailability("nonexistent@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    void getUser_WhenUserExists_ReturnsUser() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Users result = userService.getUser("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findUserByUsername("testuser");
    }

    @Test
    void signUp_CreatesNewUser() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.ROLE_MEMBER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        // Act
        Users result = userService.signUp(signUpRequestDto);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(Users.class));
        verify(passwordEncoder).encode(signUpRequestDto.getPassword());
        verify(roleRepository).findByName(RoleName.ROLE_MEMBER);
    }

    @Test
    void signIn_AuthenticatesUser() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtService.generateToken(any(Users.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act
        LoginResponseDto result = userService.signIn(loginRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.getAccessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(Users.class));
    }
}
