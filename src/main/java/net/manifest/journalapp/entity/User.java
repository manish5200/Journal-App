package net.manifest.journalapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.manifest.journalapp.enums.AccountStatus;
import net.manifest.journalapp.enums.Role;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails{

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NotBlank
 //   @Size(min = 3, max =50)
    private String username;

    @NotBlank
    private String name;

    @Indexed(unique = true)
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Set<Role> roles = new HashSet<>();
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    private boolean sentimentAnalysisEnabled = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;


    //USER DETAILS IMPL
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password; // Return the password field
    }

    @Override
    public String getUsername() {
        return username; // Return the username field
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Or logic based on your requirements
    }

    @Override
    public boolean isAccountNonLocked() {
        // You could use your AccountStatus enum here
        return accountStatus != AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or logic based on your requirements
    }

    @Override
    public boolean isEnabled() {
        // You could use your AccountStatus enum here
        return accountStatus == AccountStatus.ACTIVE;
    }
}

