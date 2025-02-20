package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.exception.*;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.*;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.RoleRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.confirmationToken.ConfirmationTokenService;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.security.service.JwtService;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ConfirmationTokenService confirmationTokenService;

    @Override
    public Boolean checkUsernameAvailability(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean checkEmailAvailability(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Users getUser(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
    }

    @Override
    public Users signUp(SignUpRequestDto signUpRequestDto) {
        if (checkEmailAvailability(signUpRequestDto.getEmail())) {
            throw new BadRequestException(new ApiResponse(Boolean.FALSE, "Email is already taken"));
        }

        if (checkUsernameAvailability(signUpRequestDto.getUsername())) {
            throw new BadRequestException(new ApiResponse(Boolean.FALSE, "Username is already taken"));
        }

        Set<String> strRoles = signUpRequestDto.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_MEMBER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "leader":
                        Role modRole = roleRepository.findByName(RoleName.ROLE_LEADER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_MEMBER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());

        Users user = new Users(
                signUpRequestDto.getFirstName(),
                signUpRequestDto.getLastName(),
                signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                passwordEncoder.encode(signUpRequestDto.getPassword()),
                LocalDate.now(),
                LocalDate.now(),
                signUpRequestDto.getProfilePicture(),
                authorities
        );

        user.setRole(roles);
        Users savedUser = userRepository.save(user);

        // Generate and save confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, savedUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        // Send confirmation email
       // confirmationTokenService.sendEmailConfirmation(savedUser.getEmail(), token);

        return savedUser;
    }


    @Override
    public LoginResponseDto signIn(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        Optional<Users> optionalUser = userRepository.findUserByUsername(username);

        Users user = optionalUser.orElseThrow(
                () -> new UsernameNotFoundException("User with username: " + username + " is not found")
        );

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(user);


        Users userDetails = (Users) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());


        return new LoginResponseDto(
                jwt,
                "Bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwtService.getExpirationTime(),
                userDetails.getProfilePicture()
        );
    }




    @Override
    public Users updateUser(Users newUser, String username, UserPrincipal currentUser) {
        Users user = userRepository.getUserByName(username);

        // Check if the current user is an admin
        boolean isAdmin = currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));

        // Check if the current user is updating their own profile
        boolean isSelf = user.getId().equals(currentUser.getId());

        // Check if the current user is a group leader
        boolean isGroupLeader = currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_LEADER.toString()));

        // If the current user is a group leader, ensure they manage the user's group
        //boolean canGroupLeaderEdit = isGroupLeader && groupRepository.isUserInGroupManagedByLeader(user.getId(), currentUser.getId());

        if (isAdmin || isSelf /*|| canGroupLeaderEdit*/) {
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setEmail(newUser.getEmail());
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            return userRepository.save(user);
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + username);
        throw new UnauthorizedException(apiResponse);
    }


    @Override
    public ApiResponse deleteUser(String username, UserPrincipal currentUser) {
        Users user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
        if (!user.getId().equals(currentUser.getId()) || !currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete profile of: " + username);
            throw new AccessDeniedException(apiResponse);
        }

        userRepository.deleteById(user.getId());

        return new ApiResponse(Boolean.TRUE, "You successfully deleted profile of: " + username);
    }

    @Override
    public ApiResponse giveAdmin(String username) {
        Users user = userRepository.getUserByName(username);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("User role not set")));
        roles.add(roleRepository.findByName(RoleName.ROLE_LEADER)
                .orElseThrow(() -> new AppException("User role not set")));
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + username);
    }

    @Override
    public ApiResponse removeAdmin(String username) {
        Users user = userRepository.getUserByName(username);
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_LEADER).orElseThrow(() -> new AppException("User role not set")));
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + username);
    }

    @Override
    public ApiResponse giveGroupLeader(String username) {
        Users user = userRepository.getUserByName(username);
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_LEADER).orElseThrow(() -> new AppException("User role not set")));
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You give group leader role to user: " + username);
    }

    @Override
    public ApiResponse removeGroupLeader(String username) {
        Users user = userRepository.getUserByName(username);
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You took group leader role from user: " + username);
    }

    @Override
    public void save(Users user) {
        userRepository.save(user);
    }
}
