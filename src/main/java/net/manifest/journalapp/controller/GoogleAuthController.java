package net.manifest.journalapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.dto.auth.JwtResponseDTO;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.Role;
import net.manifest.journalapp.repository.UserRepository;
import net.manifest.journalapp.services.UserDetailsServiceImpl;
import net.manifest.journalapp.services.UserService;
import net.manifest.journalapp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A controller for handling a manual OAuth2 login flow with Google.
 * NOTE: This is a manual implementation. For most production scenarios, the automated
 * Spring Security OAuth2 Client (`.oauth2Login()`) is the recommended, more secure approach.
 */
@Slf4j
@RestController
@RequestMapping("/auth/google")
@Tag(name = "Google Auth API (Manual Flow)", description = "Endpoints for manual Google OAuth2 authentication")
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;

    /**
     * This is the callback endpoint that Google redirects to after a user grants permission.
     * It receives the authorization code and handles the entire token exchange and user processing flow.
     * @param code The single-use authorization code from Google.
     * @return A ResponseEntity containing the application's own JWT on success.
     */

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "Callback endpoint for Google OAuth2 flow")
    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallBack(@RequestParam String code) {

        try {

            // Step 1: Exchange the authorization code for an ID token from Google.
            String tokenEndPoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            // This redirect_uri MUST exactly match one of the URIs in your Google Cloud Console credentials.
            params.add("redirect_uri", "https://developers.google.com/oauthplayground");
            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, new HttpHeaders());
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndPoint, request, Map.class);

            // We get the ACCESS TOKEN, which is the key to the user's data.
            String accessToken = (String) tokenResponse.getBody().get("access_token");

            // Use the access token to get the user's full profile information.
            // This is the correct, standard endpoint for fetching user profile data.
            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

            // We must present the access token in the Authorization header.
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoRequest, Map.class);

            if (userInfoResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> userInfo = userInfoResponse.getBody();

                String email = (String) userInfo.get("email");
                String name = (String) userInfo.get("name");

                User user = userService.findOrCreateOauthUser(userInfo);

                String jwt = jwtUtils.generateToken(user.getUsername());

                List<String> roles = user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponseDTO(jwt, user.getUsername(), roles));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not get user info from Google.");

        } catch (Exception e) {
            log.error("An error occurred during Google authentication: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during Google authentication: " + e.getMessage());
        }
    }
}

