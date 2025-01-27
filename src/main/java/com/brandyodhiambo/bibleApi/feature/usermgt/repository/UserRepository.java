package com.brandyodhiambo.bibleApi.feature.usermgt.repository;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.UserDetailsImpl;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDetailsImpl,Long> {

    Optional<UserDetailsImpl> findUserByUsername(@NotBlank String username);
    Optional<UserDetailsImpl> findUserByEmail(@NotBlank String email);

    Boolean existsByUsername(@NotBlank String username);

    Boolean existsByEmail(@NotBlank String email);

    Optional<UserDetailsImpl> findByUsernameOrEmail(String username, String email);

    default UserDetailsImpl getUserByName(String username) {
        return findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

}
