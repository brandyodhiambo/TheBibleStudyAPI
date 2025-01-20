package com.brandyodhiambo.bibleApi.feature.usermgt.repository;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findUserByUsername(@NotBlank String username);
    Optional<User> findUserByEmail(@NotBlank String email);

    Boolean existByUsername(@NotBlank String username);

    Boolean existByEmail(@NotBlank String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    default User getUserByName(String username) {
        return findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

}
