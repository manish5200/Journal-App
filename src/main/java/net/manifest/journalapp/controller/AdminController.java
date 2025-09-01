package net.manifest.journalapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.cache.AppCache;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.services.JournalEntryService;
import net.manifest.journalapp.services.UserService;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin APIs", description = "Endpoints for user management and content moderation")
// Only users with the 'ROLE_ADMIN' can access these APIs.
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private AppCache appCache;


    /**
     * Retrieves a list of all users in the system.
     * @return A ResponseEntity containing a list of all User objects.
     */
    @Operation(summary = "Get a list of all users in the system")
    @GetMapping("/users")
    public ResponseEntity<List<User>>getAllUsers(){
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Deletes a user and all of their associated data (journals, summaries).
     * This is a highly destructive operation.
     * @param userId The ID of the user to delete.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Delete a user.")
    @DeleteMapping("/users/Id/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId){
            final ObjectId id;
            try{
                id = new ObjectId(userId);
            }catch (IllegalArgumentException ex){
                return ResponseEntity.badRequest().body("Invalid ObjectId: "+ userId);
            }
            try{
                userService.deleteUserAndAssociatedData(id);
                log.warn("ADMIN ACTION: Deleted user with ID: {}", userId);
                return ResponseEntity.ok("User and all associated data deleted successfully.");
            } catch (Exception e) {
                log.error("ADMIN ACTION: Error deleting user with ID: {}", userId, e);
                return ResponseEntity.internalServerError().body("Error deleting user.");
            }
    }

    /**
     * Promotes a user to have ADMIN privileges by adding the ROLE_ADMIN to their roles.
     * @param userId The ID of the user to promote.
     * @return A ResponseEntity indicating the result of the operation.
     */

    @Operation(summary = "Promote a user to an ADMIN")
    @PostMapping("/users/Id/{userId}/promote")
    public ResponseEntity<?>promoteUserToAdmin(@PathVariable String userId){

        final ObjectId id;
        try{
            id = new ObjectId(userId);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid ObjectId: "+ userId);
        }

        try {
            userService.promoteToAdmin(id);
            log.info("ADMIN ACTION: Promoted user with ID: {}", userId);
            return ResponseEntity.ok("User successfully promoted to ADMIN.");
        }catch(RuntimeException e) {
            log.error("Error occurred while promoting user to admin.");
            return ResponseEntity.notFound().build();
            }

    }


    /**
     * Bans a user's account, preventing them from logging in.
     * @param userId The ID of the user to ban.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Ban a user account")
    @PostMapping("/users/Id/{userId}/ban")
    public ResponseEntity<?> banUser(@PathVariable String userId) {
        final ObjectId id;
        try{
            id = new ObjectId(userId);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Invalid ObjectId: "+ userId);
        }
        try {
            userService.banUser(id);
            log.warn("ADMIN ACTION: Banned user with ID: {}", userId);
            return ResponseEntity.ok("User account has been banned.");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes any journal entry in the system for content moderation purposes.
     * This bypasses the normal ownership checks.
     * @param journalId The ID of the journal entry to delete.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Delete any journal entry for moderation")
    @DeleteMapping("/journals/Id/{journalId}")
    public ResponseEntity<?>deleteJournalEntryForModeration(@PathVariable String journalId){
        final ObjectId userJournalId;
        try{
            userJournalId = new ObjectId(journalId);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Invalid ObjectId: "+ journalId);
        }

        try{
            // We pass `null` for the user to indicate this is a privileged admin action,
            // which the service layer will use to bypass the ownership check.
            boolean deleted = journalEntryService.deleteJournalEntry(userJournalId, null);
            if (deleted){
                log.warn("ADMIN ACTION: Deleted journal entry with ID: {}", journalId);
                return ResponseEntity.ok("Journal entry deleted successfully for moderation.");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("ADMIN ACTION: Error deleting journal entry with ID: {}", journalId, e);
            return ResponseEntity.internalServerError().body("Error deleting journal entry.");
        }
    }

    //Re-Initialising my map
    @Operation(summary = "Clear App Cache")
    @GetMapping("/clear-app-cache")
    public ResponseEntity<String>clearAppCache(){
        appCache.init();
        return new ResponseEntity<>("App-Cache cleared successfully.",HttpStatus.OK);
    }
}
