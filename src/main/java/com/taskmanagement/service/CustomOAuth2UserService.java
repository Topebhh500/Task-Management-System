package com.taskmanagement.service;

import com.taskmanagement.model.User;
import com.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        
        // Check if user exists
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Create new user if not exists
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email.split("@")[0] + UUID.randomUUID().toString().substring(0, 4));
                    newUser.setAuthProvider(User.AuthProvider.GOOGLE);
                    return userRepository.save(newUser);
                });

        return new CustomOAuth2User(user, attributes);
    }
}

// Custom OAuth2 User class
class CustomOAuth2User implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Expand later for roles
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public User getUser() {
        return user;
    }
}