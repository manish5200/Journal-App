package net.manifest.journalapp.controller.journal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.dto.journal.JournalEntryDTO;
import net.manifest.journalapp.dto.journal.JournalEntryPatchDTO;
import net.manifest.journalapp.dto.journal.JournalResponseDTO;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.services.JournalEntryService;
import net.manifest.journalapp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/me/journals")
@Tag(name = "My Journal APIs", description = "Endpoints for managing the authenticated user's own journal entries")
public class UserJournalController {
    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;

     private String getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * Helper method to get the current user by fetching them from the database
     * using the username from the security context.
     * @return The full User entity for the logged-in user.
     */
    private User getCurrentUserFromDatabase() {
        String username = getAuthenticatedUser();// Get the username (String)
        // Perform a database lookup to get the full User object
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }

    
    //GET ALL JOURNALS FOR CURRENT LOGGED_IN USER

    /**
     * Retrieves a paginated list of the authenticated user's journal entries.
     * @param pageable Spring automatically populates this with query params like ?page=0&size=10&sort=createdAt,desc
     * @return A ResponseEntity containing a Page of journal entry response DTOs.
     */

    @GetMapping
    @Operation(summary = "Get all journals for current logged-in user")
    public ResponseEntity<?>getMyJournalEntries(@PageableDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        User loggedInUser = getCurrentUserFromDatabase();
        Page<JournalResponseDTO> entriesForUser = journalEntryService.getEntriesForUser(loggedInUser, pageable);
        return  ResponseEntity.ok(entriesForUser);
    }


    //FILTERING USING TAGS
    /**
     * Retrieves a paginated list of the user's journal entries that contain a specific tag.
     *
     * @param tag      The tag to filter by, passed as a query parameter (e.g., ?tag=work).
     * @param pageable Standard pagination parameters.
     * @return A ResponseEntity containing a Page of matching journal entry DTOs.
     */
    @Operation(summary = "Get my journal entries filtered by a specific tag")
    @GetMapping("/by-tag")
    public ResponseEntity<?> getMyJournalEntriesUsingTag(@RequestParam String tag ,
                                                                                @PageableDefault(sort="createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        User user = getCurrentUserFromDatabase();
        try{
            Page<JournalResponseDTO> entries = journalEntryService.getEntriesForUserByTag(user, tag, pageable);
            return ResponseEntity.ok(entries);
        }catch(Exception e){
             log.error("Error in filtering the entries using tags for user having user ID: {} and username: {}",user.getId(),user.getUsername());
             return ResponseEntity.badRequest().body("Error in filtering the entries using tags.");
        }
    }

    //CREATION - CONTROLLER

    /**
     * Creates a new journal entry for the authenticated user.
     * @param journal The request body, validated to ensure it contains required fields.
     * @return A ResponseEntity containing the newly created journal entry's DTO with a 201 CREATED status.
     */
    @PostMapping
    @Operation(summary = "Add journals for current logged-in user")
    public ResponseEntity<JournalResponseDTO>createJournal(@Valid @RequestBody JournalEntryDTO journal) {
        try{
            User currentUser = getCurrentUserFromDatabase();
            //Persist
            JournalResponseDTO journalResponseDTO = journalEntryService.saveNewEntry(journal, currentUser);
            //Build Response DTO
            return new ResponseEntity<>(journalResponseDTO,HttpStatus.CREATED);
        }catch(Exception e){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a single, specific journal entry owned by the authenticated user.
     * @param journalId The unique ID of the journal entry.
     * @return A ResponseEntity containing the found DTO, or a 404 NOT FOUND if it doesn't exist or isn't owned by the user.
     */

    @GetMapping("Id/{journalId}")
    @Operation(summary = "Get journal for current logged-in user by journal's ObjectId")
    public ResponseEntity<?>getMyJournalEntry(@PathVariable String journalId) {
        final ObjectId myJournalId;
        try{
            myJournalId = new ObjectId(journalId);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid ObjectId: " + journalId);
        }
        User loggedInUser = getCurrentUserFromDatabase();
        Optional<JournalResponseDTO> entryByIdForUser = journalEntryService.findEntryByIdForUser(myJournalId, loggedInUser);
         if(entryByIdForUser.isEmpty()){
              throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No journal found with this id");
         }
         JournalResponseDTO journalEntryResponse = entryByIdForUser.get();
         return ResponseEntity.ok(journalEntryResponse);
    }


    //REPLACE THE JOURNAL

    /**
     * Replaces an entire journal entry with new data.
     * @param journalId The ID of the entry to replace.
     * @param dto The DTO containing the full new data for the entry. Must be valid.
     * @return A ResponseEntity containing the updated DTO, or a 404 NOT FOUND.
     */

    @PutMapping("Id/{journalId}")
    @Operation(summary = "Replace an entire journal entry (PUT)")
    public ResponseEntity<?>replaceEntryById(@RequestBody JournalEntryDTO dto, @PathVariable String journalId) {
        final ObjectId myJournalId;
        try{
            myJournalId = new ObjectId(journalId);
        }catch (IllegalArgumentException ex){
            return new ResponseEntity<>("Invalid ObjectId: "+journalId,HttpStatus.BAD_REQUEST);
        }
        User loggedInUser = getCurrentUserFromDatabase();
        Optional<JournalResponseDTO> updatedEntry = journalEntryService.updatedEntry(myJournalId, dto, loggedInUser);
        if(updatedEntry.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Error in updating the entry.");
        }
        JournalResponseDTO updatedJournalEntry = updatedEntry.get();
        return ResponseEntity.ok(updatedJournalEntry);
    }

    //PATCH THE JOURNAL

    /**
     * Partially updates a journal entry. Only the fields provided in the request body will be changed.
     * @param journalId The ID of the entry to patch.
     * @param patchDto The DTO containing the fields to update. Fields can be null.
     * @return A ResponseEntity containing the updated DTO, or a 404 NOT FOUND.
     */

     @Operation(summary = "Partially update a journal entry (PATCH)")
     @PatchMapping("/Id/{journalId}")
     public ResponseEntity<?>updateJournalEntryById(@PathVariable String journalId, @RequestBody JournalEntryPatchDTO patchDto){
         //String  --> ObjectId
         final ObjectId myJournalId;
          try{
              myJournalId = new ObjectId(journalId);
          }catch (IllegalArgumentException ex){
               return ResponseEntity.badRequest().body("Invalid ObjectId: "+ journalId);
          }

          User loggedInUser = getCurrentUserFromDatabase();
          Optional<JournalResponseDTO> patchedEntryOptional = journalEntryService.patchEntry(myJournalId, patchDto, loggedInUser);
         if(patchedEntryOptional.isEmpty()){
             throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Error in patching the entry.");
         }
         return  ResponseEntity.ok(patchedEntryOptional.get());
         }

    //DELETE JOURNAL ENTRY

    /**
     * Deletes a journal entry owned by the authenticated user.
     * @param journalId The ID of the entry to delete.
     * @return A ResponseEntity with 204 NO CONTENT on success, or 404 NOT FOUND if the entry doesn't exist or isn't owned by the user.
     */

    @DeleteMapping("Id/{journalId}")
    @Operation(summary = "Delete journal for current logged-in user by journal's ObjectId")
    public ResponseEntity<String>deleteEntryById(@PathVariable String journalId){
        //String  --> ObjectId
        final ObjectId myJournalId;
        try{
            myJournalId = new ObjectId(journalId);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid ObjectId: "+ journalId);
        }

         User currentUser = getCurrentUserFromDatabase();
         boolean removed = journalEntryService.deleteJournalEntry(myJournalId,currentUser);
         if(removed) {
             return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
         }else {
              return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("No journal entry to delete for this id.");
         }
    }
}
