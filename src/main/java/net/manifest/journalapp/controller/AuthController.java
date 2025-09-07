package net.manifest.journalapp.controller;

import com.mongodb.DuplicateKeyException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.dto.auth.JwtResponseDTO;
import net.manifest.journalapp.dto.auth.LoginDTO;
import net.manifest.journalapp.dto.auth.RegistrationDTO;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.services.UserDetailsServiceImpl;
import net.manifest.journalapp.services.UserService;
import net.manifest.journalapp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
* Registration of user (public) and logins
**/
@Slf4j
@RestController
@Tag(name="Auth API",description = "User Registration,Login(Admin & User)" )
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtUtils jwtUtils;

    //Registration
    @PostMapping("/user-signup")
    @Operation(summary = "User Registration")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationDTO registrationDTO){
        if (userService.findByUsername(registrationDTO.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userService.findByEmail(registrationDTO.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        try{
            userService.registerNewUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registered successfully");
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate key");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to register");
        }
    }


    //login
    @PostMapping("/login")
    @Operation(summary = "Login - User/Admin")
    public ResponseEntity<?>login(@RequestBody LoginDTO loginDTO) {

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword())
            );
            // If authentication is successful, the code proceeds. If not, an exception is thrown.
            log.info("User '{}' authenticated successfully.", loginDTO.getUsername());
            // 2. Fetch the full user details from the database
            User user = userService.findByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

            // 3. Update the last login timestamp
            user.setLastLoginAt(LocalDateTime.now());
            userService.save(user); // Save the updated user object
            log.info("Updated lastLoginAt timestamp for user '{}'.", user.getUsername());

            // 4. Generate the JWT
            String token = jwtUtils.generateToken(user.getUsername());
            List<String> roles = user.getRoles().stream()
                    .map(role -> role.name())
                    .collect(Collectors.toList());

            log.info("Successfully logged in and JWT generated for authentication purpose");

            return ResponseEntity.ok(new JwtResponseDTO(token,
                    user.getUsername()
                    , roles));
        }catch (BadCredentialsException ex){
            log.error("Incorrect username or password. ",ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        } catch (Exception e) {
            log.error("Exception occurred while creating AuthenticationToken: "+"Login Method",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error");
        }
    }
}
