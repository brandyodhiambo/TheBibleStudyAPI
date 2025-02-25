package com.brandyodhiambo.bibleApi.feature.usermgt.repository;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findUserByUsername(@NotBlank String username);
    Optional<Users> findUserByEmail(@NotBlank String email);


    Boolean existsByUsername(@NotBlank String username);

    Boolean existsByEmail(@NotBlank String email);

    Optional<Users> findByUsernameOrEmail(String username, String email);

    default Users getUserByName(String username) {
        return findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

}
