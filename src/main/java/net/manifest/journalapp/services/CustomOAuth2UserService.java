// src/main/java/net/manifest/journalapp/services/CustomOAuth2UserService.java

package net.manifest.journalapp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.AccountStatus;
import net.manifest.journalapp.enums.Role;
import net.manifest.journalapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * If we will be using Google with the openid as scope,it's an OIDC login.
     * In an OIDC flow, all the user's information (name, email, etc.) is securely packed inside a special token called the ID Token.
     * To be more efficient, Spring Security gets the ID Token, sees that it already has all the user info it needs,
     * and skips the separate step that would call your CustomOAuth2UserService.
     * ###The Solution: Use the OIDC User Service --->
     * SOLUTION -> We will replace your CustomOAuth2UserService with a new CustomOidcUserService that is specifically designed for this type of login.
     *  // 1. Get the standard OIDC user
     public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
     OidcUser oidcUser = super.loadUser(userRequest);
     String email = oidcUser.getAttribute("email");
     String name = oidcUser.getAttribute("name");
     .
     .
     ......
     return oidcUser;
     }
     AND
     in SecurityConfig.java
     .....
     .oauth2Login(oauth2 -> oauth2
     .userInfoEndpoint(userInfo -> userInfo
     // POINT TO THE NEW OIDC SERVICE
     .oidcUserService(customOidcUserService)
     )
     **/
    //Google OAuth without openid login
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("CustomOAuth2UserService.loadUser invoked. Attributes: {}", oAuth2User.getAttributes());

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            log.error("OAuth2 provider did not return an email.");
            throw new OAuth2AuthenticationException("Email not provided by OAuth2 provider");
        }
        // WRAP THE ENTIRE DATABASE OPERATION IN A TRY-CATCH
        try {
            log.info("Processing user with email: {}. Checking if user exists in the database.", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;
            if(userOptional.isPresent()){
                user=userOptional.get();
                log.info("User with email {} found. Updating details.", email);
                user.setName(name);
                user.setLastLoginAt(LocalDateTime.now());
            }else{
                log.info("User with email {} not found. Creating a new user.", email);
                user = new User();
                user.setUsername(email);
                user.setEmail(email);
                user.setName(name);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_USER)));
                user.setAccountStatus(AccountStatus.ACTIVE);
                user.setCreatedAt(LocalDateTime.now());
                user.setLastLoginAt(LocalDateTime.now());
                log.info("Attempting to save new user with email: {}", email);
            }

            // Save and capture returned entity (MongoRepository.save returns the persisted entity)
            User saved = userRepository.save(user);
            log.info("Save returned entity: id={}, email={}, username={}", saved.getId(), saved.getEmail(), saved.getUsername());

            // Verify by re-reading from DB
            Optional<User> verify = userRepository.findByEmail(email);
            if (verify.isPresent()) {
                User v = verify.get();
                log.info("VERIFIED: user persisted. id={}, email={}, createdAt={}", v.getId(), v.getEmail(), v.getCreatedAt());
            } else {
                log.error("VERIFICATION FAILED: userRepository.findByEmail({}) returned empty even after save()", email);
                // Dump a short list (debug) to help tracing
                userRepository.findAll().forEach(u -> log.debug("Existing user: id={}, email={}", u.getId(), u.getEmail()));
            }

            log.info("Successfully saved or updated user with email: {}", email);
        } catch (Exception e) {
            log.error("!!! CRITICAL: DATABASE SAVE FAILED in CustomOAuth2UserService for email {} !!!", email, e);
            throw new OAuth2AuthenticationException("Failed to save user to database: " + e.getMessage());
        }

        return oAuth2User;
    }
}