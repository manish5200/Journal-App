package net.manifest.journalapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.manifest.journalapp.enums.AccountStatus;
import net.manifest.journalapp.enums.Role;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class User {

    // --- Primary Identifier ---
    @Id
    private ObjectId id;


    // --- Core Identity & Credentials ---
    /**
     * The unique, public-facing username for the user. Used for login.
     */
    @Indexed(unique = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private  String username;

    /**
     * The user's full name for personalization.
     */
    @NotBlank(message = "Name is required.")
    private String name;

    /**
     * The unique email address for the user. Used for login and communication.
     */
    @Indexed(unique = true)
    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    /**
     * The user's hashed password. Marked as WRITE_ONLY to prevent serialization in API responses.
     */
    @NotBlank(message = "Password is required.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    // --- Security & Authorization ---
    /**
     * A set of roles assigned to the user (e.g., ROLE_USER, ROLE_ADMIN) because set prevents duplicate roles.
     */
    private Set<Role> roles = new HashSet<>();
    /**
     * The current status of the user's account.
     */
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    // --- Application-Specific Fields ---
    /**
     * If true, the app will automatically analyze and save sentiment for new entries.
     * This is a user-controlled setting.
     */
    private boolean sentimentAnalysisEnabled = true;

    // --- Timestamps & Auditing ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

}
