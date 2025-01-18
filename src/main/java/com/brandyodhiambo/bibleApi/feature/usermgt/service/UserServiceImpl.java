package com.brandyodhiambo.bibleApi.feature.usermgt.service;

import com.brandyodhiambo.bibleApi.exception.AppException;
import com.brandyodhiambo.bibleApi.exception.BadRequestException;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Role;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.User;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.RoleRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.RoleName;
import com.sun.security.auth.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Boolean checkUsernameAvailability(String username) {
        return userRepository.existByEmail(username);
    }

    @Override
    public Boolean checkEmailAvailability(String email) {
        return userRepository.existByEmail(email);
    }

    @Override
    public User getUser(String username) {
        return userRepository.getUserByName(username);
    }

    @Override
    public User signUp(SignUpRequestDto signUpRequestDto) {
        if(checkEmailAvailability(signUpRequestDto.getEmail())){
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Username is already taken");
            throw new BadRequestException(apiResponse);
        }

        if(checkUsernameAvailability(signUpRequestDto.getUsername())){
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "User with the email is already taken");
            throw new BadRequestException(apiResponse);
        }

        User user = new User(
                signUpRequestDto.getFirstName(),
                signUpRequestDto.getLastName(),
                signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                passwordEncoder.encode(signUpRequestDto.getPassword()),
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
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new AppException("Error: Role is not found."));
                    roles.add(adminRole);
                } else if (role.equals("mod")) {
                    Role modRole = roleRepository.findByName(RoleName.ROLE_GROUP_LEADER)
                            .orElseThrow(() -> new AppException("Error: Role is not found."));
                    roles.add(modRole);
                } else {
                    Role userRole = roleRepository.findByName(RoleName.ROLE_MEMBER)
                            .orElseThrow(() -> new AppException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }
        user.setRole(roles);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User newUser, String username, UserPrincipal currentUser) {
        return null;
    }

    @Override
    public ApiResponse deleteUser(String username, UserPrincipal currentUser) {
        return null;
    }

    @Override
    public ApiResponse giveAdmin(String username) {
        return null;
    }

    @Override
    public ApiResponse removeAdmin(String username) {
        return null;
    }

    @Override
    public ApiResponse giveGroupLeader(String username) {
        return null;
    }

    @Override
    public ApiResponse removeGroupLeader(String username) {
        return null;
    }
}
