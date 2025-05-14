package com.brandyodhiambo.bibleApi.feature.usermgt.service.user;

import com.brandyodhiambo.bibleApi.exception.*;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.*;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ChangePasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ForgotPasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.ResetPasswordRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.otp.OtpService;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.RoleRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.security.service.JwtService;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

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
    private GroupRepository groupRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Boolean checkUsernameAvailability(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean checkEmailAvailability(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Users getUser(String username) {
        Users user = userRepository.getUserByName(username);
        return user;
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
                false,
                authorities
        );

        user.setRole(roles);
        Users savedUser = userRepository.save(user);
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

        // Get user's group ID
        String groupId = "";
        List<Group> userGroups = groupRepository.findByMember(user);
        if (!userGroups.isEmpty()) {
            groupId = userGroups.get(0).getId().toString();
        }

        return new LoginResponseDto(
                jwt,
                "Bearer",
                user.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwtService.getExpirationTime(),
                groupId,
                user.getFirstName(),
                user.getLastName()
        );
    }


    @Override
    public Users updateUser(SignUpRequestDto newUser, String username, Users currentUser) {
        Users user = userRepository.getUserByName(username);
        boolean isAdmin = currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));
        boolean isSelf = user.getId().equals(currentUser.getId());
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
    public void deleteUser(String username, Users currentUser) {
        Users user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
        boolean isAdmin = currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));
        boolean isSelf = user.getId().equals(currentUser.getId());
        if(isAdmin || isSelf){
            userRepository.deleteById(user.getId());
        } else{
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete profile of: " + username);
            throw new AccessDeniedException(apiResponse);
        }
    }

    @Override
    @Transactional
    public ApiResponse giveAdmin(String username) {
        Users user = userRepository.getUserByName(username);
        if (user == null) {
            throw new AppException("User not found");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("User role not set")));
        roles.add(roleRepository.findByName(RoleName.ROLE_LEADER)
                .orElseThrow(() -> new AppException("User role not set")));
        roles.add(roleRepository.findByName(RoleName.ROLE_MEMBER)
                .orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
        user.setAuthorities(authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + username);
    }


    @Override
    public ApiResponse removeAdmin(String username) {
        Users user = userRepository.getUserByName(username);
        if (user == null) {
            throw new AppException("User not found");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_LEADER).orElseThrow(() -> new AppException("User role not set")));
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
        user.setAuthorities(authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + username);
    }

    @Override
    public ApiResponse giveGroupLeader(String username) {
        Users user = userRepository.getUserByName(username);
        if (user == null) {
            throw new AppException("User not found");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_LEADER).orElseThrow(() -> new AppException("User role not set")));
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
        user.setAuthorities(authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You give group leader role to user: " + username);
    }

    @Override
    public ApiResponse removeGroupLeader(String username) {
        Users user = userRepository.getUserByName(username);
        if(user ==null){
            throw new AppException("User not found");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_MEMBER).orElseThrow(() -> new AppException("User role not set")));
        user.setRole(roles);
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
        user.setAuthorities(authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userRepository.save(user);
        return new ApiResponse(Boolean.TRUE, "You took group leader role from user: " + username);
    }

    @Override
    public void save(Users user) {
        userRepository.save(user);
    }

    @Autowired
    private OtpService otpService;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void forgotPassword(ForgotPasswordRequestDto requestDto) {
        String email = requestDto.getEmail();
        Users user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String token = otpService.generateAndStoreOtp(user.getId());

        // Send password reset email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setFrom("System");

        String resetUrl = "http://localhost:8005/reset-password?uid=" + user.getId() + "&token=" + token;
        String emailText = "Click the link to reset your password: " + resetUrl + 
                "\nThis link will expire in 5 minutes.";

        message.setText(emailText);
        mailSender.send(message);
    }

    @Override
    public ApiResponse resetPassword(ResetPasswordRequestDto requestDto) {
        // Validate token
        if (!otpService.isOtpValid(requestDto.getUserId(), requestDto.getToken())) {
            return new ApiResponse(Boolean.FALSE, "Invalid or expired token");
        }

        // Validate passwords match
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            return new ApiResponse(Boolean.FALSE, "Passwords do not match");
        }

        // Get user
        Users user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.getUserId().toString()));

        // Update password
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);

        // Delete token
        otpService.deleteOtp(requestDto.getUserId());

        return new ApiResponse(Boolean.TRUE, "Password reset successfully");
    }

    @Override
    public ApiResponse changePassword(String username, ChangePasswordRequestDto requestDto) {
        // Get user
        Users user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " is not found"));

        // Validate current password
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            return new ApiResponse(Boolean.FALSE, "Current password is incorrect");
        }

        // Validate passwords match
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            return new ApiResponse(Boolean.FALSE, "Passwords do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);

        return new ApiResponse(Boolean.TRUE, "Password changed successfully");
    }
}
