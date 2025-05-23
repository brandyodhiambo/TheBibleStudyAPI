package com.brandyodhiambo.bibleApi.feature.usermgt.models.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Set;

@Data
public class SignUpRequestDto {

    @NotEmpty(message = "User first name must be provided")
    private String firstName;

    @NotEmpty(message = "User last name must be provided")
    private String lastName;

    @NotEmpty(message = "Username must be provided")
    private String username;

    @NotEmpty(message = "User email must be provided")
    private String email;

    @NotEmpty(message = "User password must be provided")
    private String password;

    @NotEmpty(message = "User role must be provided")
    private Set<String> role;


    public SignUpRequestDto( String firstName, String lastName, String username, String email, String password, Set<String> role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public @NotEmpty(message = "User first name must be provided") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotEmpty(message = "User first name must be provided") String firstName) {
        this.firstName = firstName;
    }

    public @NotEmpty(message = "User last name must be provided") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotEmpty(message = "User last name must be provided") String lastName) {
        this.lastName = lastName;
    }

    public @NotEmpty(message = "Username must be provided") String getUsername() {
        return username;
    }

    public void setUsername(@NotEmpty(message = "Username must be provided") String username) {
        this.username = username;
    }

    public @NotEmpty(message = "User email must be provided") String getEmail() {
        return email;
    }

    public void setEmail(@NotEmpty(message = "User email must be provided") String email) {
        this.email = email;
    }

    public @NotEmpty(message = "User password must be provided") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "User password must be provided") String password) {
        this.password = password;
    }

    public @NotEmpty(message = "User role must be provided") Set<String> getRole() {
        return role;
    }

    public void setRole(@NotEmpty(message = "User role must be provided") Set<String> role) {
        this.role = role;
    }
}
