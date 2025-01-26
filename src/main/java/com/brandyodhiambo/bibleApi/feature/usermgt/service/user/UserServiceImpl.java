package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.exception.*;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.*;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.RoleRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.confirmationToken.ConfirmationTokenService;
import com.brandyodhiambo.bibleApi.security.jwt.JwtUtils;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserDetailsImpl;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtil;

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
    public UserDetailsImpl getUser(String username) {
        return userRepository.getUserByName(username);
    }

    @Override
    public UserDetailsImpl signUp(SignUpRequestDto signUpRequestDto) {
        if (!checkEmailAvailability(signUpRequestDto.getEmail())) {
            throw new BadRequestException(new ApiResponse(Boolean.FALSE, "Email is already taken"));
        }

        if (!checkUsernameAvailability(signUpRequestDto.getUsername())) {
            throw new BadRequestException(new ApiResponse(Boolean.FALSE, "Username is already taken"));
        }

        UserDetailsImpl user = new UserDetailsImpl(
                signUpRequestDto.getFirstName(),
                signUpRequestDto.getLastName(),
                signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                passwordEncoder.encode(signUpRequestDto.getPassword()),
                LocalDate.now(),
                LocalDate.now(),
                signUpRequestDto.getProfilePicture()
        );

        List<Role> strRoles = signUpRequestDto.getRole();
        List<Role> roles = new ArrayList<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_MEMBER)
                    .orElseThrow(() -> new AppException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role resolvedRole = roleRepository.findByName(
                        role.equals("admin") ? RoleName.ROLE_ADMIN :
                                role.equals("leader") ? RoleName.ROLE_LEADER : RoleName.ROLE_MEMBER
                ).orElseThrow(() -> new AppException("Error: Role is not found."));
                roles.add(resolvedRole);
            });
        }
        user.setRole(roles);

        UserDetailsImpl savedUser = userRepository.save(user);

        // Generate and save confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, savedUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        // Send confirmation email
        confirmationTokenService.sendEmailConfirmation(savedUser.getEmail(), token);

        return savedUser;
    }


    @Override
    public LoginResponseDto signIn(LoginRequestDto loginRequestDto) {
        String identifier = loginRequestDto.getUsername(); // This can be a username or an email
        String password = loginRequestDto.getPassword();

        boolean isEmail = identifier.contains("@");
        Optional<UserDetailsImpl> user = isEmail
                ? userRepository.findUserByEmail(identifier)
                : userRepository.findUserByUsername(identifier);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with " + (isEmail ? "email: " : "username: ") + identifier);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.get().getUsername(), password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new LoginResponseDto(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwtService.getExpirationTime()
        );
    }



    @Override
    public UserDetailsImpl updateUser(UserDetailsImpl newUser, String username, UserPrincipal currentUser) {
        UserDetailsImpl user = userRepository.getUserByName(username);

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
        UserDetailsImpl user = userRepository.findUserByUsername(username)
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
        UserDetailsImpl user = userRepository.getUserByName(username);
        List<Role> roles = new ArrayList<>();
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
        UserDetailsImpl user = userRepository.getUserByName(username);
        List<Role> roles = new ArrayList<>();
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
        UserDetailsImpl user = userRepository.getUserByName(username);
        List<Role> roles = new ArrayList<>();
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
        UserDetailsImpl user = userRepository.getUserByName(username);
        List<Role> roles = new ArrayList<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You took group leader role from user: " + username);
    }
}
