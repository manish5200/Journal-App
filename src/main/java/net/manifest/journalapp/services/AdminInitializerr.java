package net.manifest.journalapp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.AccountStatus;
import net.manifest.journalapp.enums.Role;
import net.manifest.journalapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class AdminInitializerr implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // These values are injected from your application.yml file
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.name}")
    private String adminName;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists to prevent creating duplicates on every restart
        if (userRepository.existsByRolesContains(Role.ROLE_ADMIN)) {
            System.out.println("Admin user already exists. Skipping creation.");
            return;

        }

        System.out.println("No admin user found. Creating initial admin...");
        log.info("No admin user found. Creating initial admin...");
        User adminUser = new User();
        adminUser.setUsername(adminUsername);
        adminUser.setName(adminName);
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword)); // Always hash the password

        // --- This is the crucial part ---
        // An admin has both USER and ADMIN roles
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_ADMIN);
        adminUser.setRoles(roles);

        adminUser.setAccountStatus(AccountStatus.ACTIVE);
        adminUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(adminUser);
        System.out.println("Initial admin user created successfully.");
        log.info("Initial admin user created successfully.");
    }
}