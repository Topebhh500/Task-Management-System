package com.taskmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name; // Added name field with validation

    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    public enum AuthProvider {
        LOCAL, GOOGLE
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // We'll expand this later for role-based auth
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
