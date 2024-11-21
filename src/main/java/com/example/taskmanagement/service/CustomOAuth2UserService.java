package com.example.taskmanagement.service;

import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            logger.debug("OAuth2User attributes: {}", oauth2User.getAttributes());

            // Extract email from Google account
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String googleId = oauth2User.getAttribute("sub");

            if (email == null) {
                logger.error("Email is null in OAuth2User attributes");
                throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
            }

            // Check if user exists
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;

            if (userOptional.isPresent()) {
                // Update existing user
                logger.debug("Updating existing user with email: {}", email);
                user = userOptional.get();
                user.setGoogleId(googleId);
            } else {
                // Create new user
                logger.debug("Creating new user with email: {}", email);
                user = new User();
                user.setEmail(email);
                user.setFullName(name);
                user.setGoogleId(googleId);
                user.setEnabled(true);
                user.setPassword(""); // Empty password for OAuth users
                user.setRoles(Collections.singleton("ROLE_USER"));
            }

            user = userRepository.save(user);
            logger.debug("User saved successfully: {}", user.getEmail());
            
            return oauth2User;
            
        } catch (Exception e) {
            logger.error("Error in OAuth2 user processing", e);
            throw new OAuth2AuthenticationException(null, "Error processing OAuth2 user", e);
        }
    }
}