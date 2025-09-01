package net.manifest.journalapp.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.dto.auth.ChangePasswordDTO;
import net.manifest.journalapp.dto.auth.RegistrationDTO;
import net.manifest.journalapp.dto.auth.UserUpdateDTO;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.AccountStatus;
import net.manifest.journalapp.enums.Role;
import net.manifest.journalapp.repository.JournalEntryRepository;
import net.manifest.journalapp.repository.UserRepository;
import net.manifest.journalapp.repository.WeeklySummaryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private JournalEntryRepository journalEntryRepository;
        @Autowired
        private WeeklySummaryRepository weeklySummaryRepository;


        private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //Extract username
        public String authenticatedUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
       }

        //Registration
        public void registerNewUser(RegistrationDTO registrationDTO){
            try{
                String hashedPassword = passwordEncoder.encode(registrationDTO.getPassword());
                User newUser = new User();
                newUser.setUsername(registrationDTO.getUsername());
                newUser.setPassword(hashedPassword);
                newUser.setName(registrationDTO.getName());
                newUser.setEmail(registrationDTO.getEmail());
                newUser.setRoles(Collections.singleton(Role.ROLE_USER));
                newUser.setAccountStatus(AccountStatus.ACTIVE);
                newUser.setSentimentAnalysisEnabled(registrationDTO.isSentimentAnalysisEnabled());
                newUser.setCreatedAt(LocalDateTime.now());
                userRepository.save(newUser);
                log.info("New user registered successfully.");
            } catch (Exception e){
                log.error("having error in {} {} : ", registrationDTO.getUsername(),e.getMessage());
                log.debug("having bug in {} {} : ",registrationDTO.getUsername(),e.getMessage());
            }
        }

        //Update user details
        public User updateUserProfile(String username, UserUpdateDTO userUpdateDTO){
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            if(userUpdateDTO.getName() != null){
                user.setName(userUpdateDTO.getName());
            }
            if(userUpdateDTO.getEmail() != null){
                user.setEmail(userUpdateDTO.getEmail());
            }
            user.setSentimentAnalysisEnabled(userUpdateDTO.isSentimentAnalysisEnabled());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }


        //***** CHANGE PASSWORD *****//

    /**
     * Allows a user to change their password after verifying their current one.
     * @param username The user changing their password.
     * @param passwordDTO The DTO containing the current and new passwords.
     */
    public void changePassword(String username, ChangePasswordDTO passwordDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            log.error("Invalid or incorrect current password.");
            throw new RuntimeException("Invalid or incorrect current password, Enter correct password.");
        }
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<User>findAll(){
          return  userRepository.findAll();
    }

    public  Optional<User> findByUsername(String username){
           return userRepository.findByUsername(username);
    }

    public Optional<Object> findByEmail(@NonNull String email) {
            return userRepository.findByEmail(email);
    }



    // --------------------------------------------------------------------
    // --- NEW METHODS FOR ADMIN CONTROLLER ---
    // --------------------------------------------------------------------

    //****** Promote USER to ADMIN (Only Admin can do) ******* //
    public void promoteToAdmin(ObjectId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.getRoles().add(Role.ROLE_ADMIN);
        userRepository.save(user);
    }

     /****** BAN USER (Only ADMIN can do) *****/
    public void banUser(ObjectId userId){
           User user = userRepository.findById(userId)
                   .orElseThrow(() -> new RuntimeException("User not found with id: "+userId));
           user.setAccountStatus(AccountStatus.BANNED);
           userRepository.save(user);
    }


    //DELETE USER BY ADMIN AND USER
    /**
     * Deletes a user and all of their associated data (journal entries, weekly summaries).
     * This is a destructive, transactional operation.
     * @param userId The ID of the user to delete.
     */
    @Transactional
    public void deleteUserAndAssociatedData(ObjectId userId){
         if(!userRepository.existsById(userId)){
             log.error("User not found with id {}",userId);
             throw new RuntimeException("User not found with id: " + userId);
         }

         //1. Delete the associated data
        journalEntryRepository.deleteAllByUserId(userId);
         //2. Delete the associated weekly summary
        weeklySummaryRepository.deleteByUserId(userId);  // will implement in future
         //3. Delete the user
        userRepository.deleteById(userId);

    }

}
