package com.brandyodhiambo.bibleApi.feature.usermgt.controller;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserPrincipal;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.LoginResponseDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.user.UserService;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserDetailsImpl;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam String username) {
        return ResponseEntity.ok(userService.checkUsernameAvailability(username));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        return ResponseEntity.ok(userService.checkEmailAvailability(email));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDetailsImpl> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserDetailsImpl> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        UserDetailsImpl user = userService.signUp(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<LoginResponseDto> signIn(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = userService.signIn(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/user/{username}")
    public ResponseEntity<UserDetailsImpl> updateUser(
            @PathVariable String username,
            @RequestBody UserDetailsImpl updatedUser,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UserDetailsImpl user = userService.updateUser(updatedUser, username, currentUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/user/{username}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable String username,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ApiResponse response = userService.deleteUser(username, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{username}/giveAdmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> giveAdmin(@PathVariable String username) {
        ApiResponse response = userService.giveAdmin(username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}/removeAdmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> removeAdmin(@PathVariable String username) {
        ApiResponse response = userService.removeAdmin(username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{username}/group-leader")
    public ResponseEntity<ApiResponse> giveGroupLeader(@PathVariable String username) {
        ApiResponse response = userService.giveGroupLeader(username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}/group-leader")
    public ResponseEntity<ApiResponse> removeGroupLeader(@PathVariable String username) {
        ApiResponse response = userService.removeGroupLeader(username);
        return ResponseEntity.ok(response);
    }
}

