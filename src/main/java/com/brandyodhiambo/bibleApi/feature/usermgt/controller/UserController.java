package com.brandyodhiambo.bibleApi.feature.usermgt.controller;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.SignUpRequestDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserDto;
import com.brandyodhiambo.bibleApi.feature.usermgt.service.user.UserService;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getUser")
    public ResponseEntity<UserDto> getUser(@RequestParam String username) {
        Users user = userService.getUser(username);
        user.getRole().size();
        return ResponseEntity.ok(new UserDto(user));
    }

    @PutMapping("/update/user/{username}")
    public ResponseEntity<Users> updateUser(
            @PathVariable String username,
            @RequestBody SignUpRequestDto updatedUser,
            @AuthenticationPrincipal Users currentUser) {
        Users user = userService.updateUser(updatedUser, username, currentUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/user/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username, @AuthenticationPrincipal Users currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You are not authorized to delete this user"));
        }
        userService.deleteUser(username, currentUser);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
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

    @PostMapping("/{username}/give-group-leader")
    public ResponseEntity<ApiResponse> giveGroupLeader(@PathVariable String username) {
        ApiResponse response = userService.giveGroupLeader(username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}/remove-group-leader")
    public ResponseEntity<ApiResponse> removeGroupLeader(@PathVariable String username) {
        ApiResponse response = userService.removeGroupLeader(username);
        return ResponseEntity.ok(response);
    }
}

