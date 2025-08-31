package net.manifest.journalapp.controller.journal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/journals/")
@Tag(name = "Public Journal APIs", description = "Endpoints for discovering and interacting with public entries")
public class PublicJournalController {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;


    /**
     * Securely retrieves the full User entity for the currently logged-in user.
     * @return The fully populated User entity from the database.
     */
    private User getCurrentUserFromDatabase() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database: " + username));
    }


    /**
     * Retrieves a paginated list of all journal entries that have been marked as public.
     * @param pageable Spring automatically populates this with query params for pagination and sorting.
     * @return A ResponseEntity containing a Page of public journal entry DTOs.
     */

    @Operation(summary = "Get all public journal entries (paginated)")
    @GetMapping
    public ResponseEntity<Page<JournalResponseDTO>> getPublicJournalEntries(
           @PageableDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        Page<JournalResponseDTO> publicEntries = journalEntryService.getPublicEntries(pageable);
        return ResponseEntity.ok(publicEntries);
    }



    /**
     * Retrieves a single public journal entry by its unique ID.
     * @param journalId The ID of the public entry to fetch.
     * @return A ResponseEntity containing the found DTO, or 404 NOT FOUND if it doesn't exist or is not public.
     */
    @Operation(summary = "Get a single public journal entry by its ID")
    @GetMapping("/Id/{journalId}")
    public ResponseEntity<?> getPublicJournalEntryById(@PathVariable String journalId) {
        final ObjectId myJournalId;
        try{
            myJournalId = new ObjectId(journalId);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid ObjectId: "+ journalId);
        }

        return journalEntryService.findPublicEntryById(myJournalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Allows an authenticated user to post a comment on a public journal entry.
     * @param journalId The ID of the public entry to comment on.
     * @param commentText The body of the comment. In a real app, this would be a DTO with validation.
     * @return A ResponseEntity indicating success or failure.
     */

    @Operation(summary = "Post a comment on a public journal entry")
    @PostMapping("/Id/{journalId}/comment")
    public ResponseEntity<?>addComment(@PathVariable String journalId,
                                       @RequestBody String commentText){ // This should be a CommentDTO in a production app
        final ObjectId myJournalId;
        try{
            myJournalId = new ObjectId(journalId);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid ObjectId: "+ journalId);
        }
        User currentUser = getCurrentUserFromDatabase();
        try {
            journalEntryService.addComment(myJournalId, commentText, currentUser);
            return ResponseEntity.ok("Comment added successfully.");
        } catch (RuntimeException e) {
            // This will catch errors like "Journal not found or not public" from the service
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Allows an authenticated user to submit a rating for a public journal entry.
     * @param journalId The ID of the public entry to rate.
     * @param rating The numerical rating. In a real app, this would be a DTO with @Min/@Max validation.
     * @return A ResponseEntity indicating success or failure.
     */

    @Operation(summary = "Rate a public journal entry")
    @PostMapping("/Id/{journalId}/rate")
    public ResponseEntity<?>addRating( @PathVariable String journalId,
                                       @RequestBody int rating){ // This should be a RatingDTO with validation

        final ObjectId myJournalId;
        try{
            myJournalId = new ObjectId(journalId);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid ObjectId: "+ journalId);
        }
        User currentUser = getCurrentUserFromDatabase();
        try {
            journalEntryService.addRating(myJournalId, rating, currentUser);
            return ResponseEntity.ok("Rating submitted successfully.");
        } catch (RuntimeException e) {
            // This will catch errors like "Journal not found" or "Invalid rating value"
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
