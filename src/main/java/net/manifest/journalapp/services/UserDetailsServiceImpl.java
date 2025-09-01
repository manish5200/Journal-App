package net.manifest.journalapp.services;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.Role;
import net.manifest.journalapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
     @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User>userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            // Convert the Set<Role> into a String array of role names
            String[] roles = user.getRoles().stream()
                    .map(Role::name)// e.g., Role.ROLE_USER becomes "ROLE_USER"
                    .map(roleName -> roleName.substring(5)) // Removes the "ROLE_" prefix
                    .toArray(String[]::new); // Converts the stream to a String array ["USER", "ADMIN"]
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(roles)  // Pass the correctly formatted String array
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username: "+ username);
    }
}
