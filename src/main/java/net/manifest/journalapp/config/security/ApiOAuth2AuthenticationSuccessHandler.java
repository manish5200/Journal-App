package net.manifest.journalapp.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.dto.auth.JwtResponseDTO;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.Role;
import net.manifest.journalapp.services.UserService;
import net.manifest.journalapp.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles successful OAuth2 authentications by issuing a JWT.
 * After a user is successfully authenticated by the OAuth2 provider and processed by the
 * user service, this handler is invoked. Its primary responsibility is to look up the
 * application's local User entity, generate a JWT for that user, and write it
 * to the HTTP response body.
 */

@Slf4j
@Component
public class ApiOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;
    /**
     * Called by Spring Security when a user has been successfully authenticated.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authentication the Spring Security Authentication object, containing the principal
     * @throws IOException if an error occurs writing to the response
     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        clearAuthenticationAttributes(request);
        String email = null;
        // --- Request-level debug info
        log.info("onAuthenticationSuccess invoked: method={}, uri={}, remoteAddr={}, thread={}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), Thread.currentThread().getName()
        );
        try{
            log.info("Authentication success for principal: {}. Preparing to issue JWT.", authentication.getName());
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            email = oauth2User.getAttribute("email");
            if (email == null) {
                log.error("Cannot issue JWT. Email attribute is null in OAuth2User principal.");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not found from OAuth2 provider");
                return;
            }
            log.info("Attempting to find user in local database with email: {}", email);
            Optional<User> userOptional = userService.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = jwtUtils.generateToken(user.getUsername());
                List<String> roles = user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toList());
                JwtResponseDTO jwtResponse = new JwtResponseDTO(token, user.getUsername(), roles);

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(jwtResponse));
                log.info("JWT generated and sent successfully for user: {}", user.getUsername());
            } else {
                log.error("User with email [{}] not found in database. Cannot issue JWT. This indicates a failure in the user provisioning step.", email);
                throw new RuntimeException("Authenticated user not found in our database: " + email);
            }

        } catch (Exception e) {
            log.error("Unexpected error occurred while creating JWT for user with email [{}].", email, e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred while processing authentication.");
            }
        }
    }
}