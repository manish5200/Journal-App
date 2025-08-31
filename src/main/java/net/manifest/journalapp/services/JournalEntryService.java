package net.manifest.journalapp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.dto.journal.JournalEntryDTO;
import net.manifest.journalapp.dto.journal.JournalEntryPatchDTO;
import net.manifest.journalapp.dto.journal.JournalResponseDTO;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.mapper.JournalMapper;
import net.manifest.journalapp.repository.*;
import net.manifest.journalapp.entity.JournalEntry;
import net.manifest.journalapp.utils.journalutils.Comment;
import net.manifest.journalapp.utils.journalutils.RatingStats;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;


    //GET ALL ENTRIES FOR A USER
    @Transactional
    public Page<JournalResponseDTO> getEntriesForUser(User user , Pageable pageable){
        try{
            Page<JournalEntry>entriesPage = journalEntryRepository.findByUserId(user.getId(),pageable);
            return entriesPage.map(entry ->JournalMapper.toResponseDTO(entry,user.getUsername()));
        }catch (Exception e) {
            log.error("Error saving new journal entry for user: {}", user.getUsername(), e);
            throw new RuntimeException("Could not save journal entry.", e);
        }
    }

    //CREATE
    @Transactional
    public JournalResponseDTO saveNewEntry(JournalEntryDTO dto, User user) {
        try {
            // 1. Use Mapper to convert DTO to an entity
              JournalEntry newEntry = JournalMapper.toEntity(dto);
            // 2. Set fields that are not in the DTO (ownership, timestamps)
              newEntry.setUserId(user.getId());
              newEntry.setCreatedAt(LocalDateTime.now());
            // 3. Save the entity to the database
            JournalEntry savedEntry = journalEntryRepository.save(newEntry);
            log.info("New journal entry saved with ID: {} for user: {}", savedEntry.getId(), user.getUsername());

            return JournalMapper.toResponseDTO(savedEntry,user.getUsername());
        }catch(Exception e){
            log.error("Error saving new journal entry for user: {}", user.getUsername(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create journal");
        }
    }

    //READ ONE JOURNAL FOR A USER
    @Transactional
    public Optional<JournalResponseDTO> findEntryByIdForUser(ObjectId journalId, User user){
            try{
                Optional<JournalEntry> journalEntryOptional = journalEntryRepository.findByIdAndUserId(journalId, user.getId());
                // The .map() function correctly handles the Optional.
                // It takes the JournalEntry *out* of the Optional and passes it to the mapper.
                // The result is an Optional<JournalResponseDTO>.
                return journalEntryOptional.map(journalEntry ->
                    JournalMapper.toResponseDTO(journalEntry, user.getUsername()));
            } catch (Exception e) {
                log.error("Error finding journal entry {} for user {}",journalId, user.getUsername(), e);
                throw new RuntimeException("Could not find journal entry.", e);
            }
    }

    //UPDATE JOURNAL ENTRY - WHOLE
    @Transactional
    public Optional<JournalResponseDTO>updatedEntry(ObjectId journalId,JournalEntryDTO journalDTO,User user){
        try {
            Optional<JournalEntry> entryToUpdateOptional = journalEntryRepository.findByIdAndUserId(journalId, user.getId());
            if(entryToUpdateOptional.isEmpty()){
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"No journal entry found.");
            }
            JournalEntry entryToUpdate = entryToUpdateOptional.get();
            entryToUpdate.setTitle(journalDTO.getTitle());
            entryToUpdate.setContent(journalDTO.getContent());
            entryToUpdate.setMood(journalDTO.getMood());
            entryToUpdate.setLocation(journalDTO.getLocation());
            entryToUpdate.setWeather(journalDTO.getWeather());
            entryToUpdate.setPublic(journalDTO.getIsPublic());
            entryToUpdate.setUpdatedAt(LocalDateTime.now());
            JournalEntry savedEntry = journalEntryRepository.save(entryToUpdate);
            log.info("Replaced (PUT) journal entry with ID: {}", savedEntry.getId());
            return Optional.of(JournalMapper.toResponseDTO(savedEntry, user.getUsername()));
        } catch (Exception e) {
            log.error("Error updating (PUT) journal entry {} for user {}", journalId, user.getUsername(), e);
            throw new RuntimeException("Could not update journal entry.", e);
        }
    }

    //PATH THE JOURNAL ENTRY
    @Transactional
    public Optional<JournalResponseDTO>patchEntry(ObjectId journalId, JournalEntryPatchDTO patchDto, User user){
        try{
            Optional<JournalEntry> journalToUpdateOptional = journalEntryRepository.findByIdAndUserId(journalId, user.getId());
            if(journalToUpdateOptional.isEmpty()){
               throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"No journal entry found.");
            }
            JournalEntry entryToUpdate = journalToUpdateOptional.get();
            if (patchDto.getTitle() != null) entryToUpdate.setTitle(patchDto.getTitle());
            if (patchDto.getContent() != null) entryToUpdate.setContent(patchDto.getContent());
            if (patchDto.getMood() != null) entryToUpdate.setMood(patchDto.getMood());
            if (patchDto.getLocation() != null) entryToUpdate.setLocation(patchDto.getLocation());
            if (patchDto.getWeather() != null) entryToUpdate.setWeather(patchDto.getWeather());
            if (patchDto.getIsPublic() != null) entryToUpdate.setPublic(patchDto.getIsPublic());
            entryToUpdate.setUpdatedAt(LocalDateTime.now());

            JournalEntry savedEntry = journalEntryRepository.save(entryToUpdate);
            log.info("Patched journal entry with ID: {}", savedEntry.getId());
            return Optional.of(JournalMapper.toResponseDTO(savedEntry, user.getUsername()));
        } catch (RuntimeException e) {
            log.error("Error patching journal entry {} for user {}", journalId, user.getUsername(), e);
            throw new RuntimeException("Could not patch journal entry.", e);
        }
    }

    //DELETE JOURNAL ENTRY

    /**
     * Deletes a journal entry. Enforces ownership rules for regular users.
     * An admin can bypass the ownership check by passing a null user object.
     * @param journalId The ID of the entry to delete.
     *@param user The user attempting to delete the entry, or null if it's an admin action.
     * @return true if the entry was deleted, false otherwise.
     */
    @Transactional
    public boolean deleteJournalEntry(ObjectId journalId,User user){
         try{
             Optional<JournalEntry>journalEntryOptional = (user==null)
                     ? journalEntryRepository.findById(journalId) // Admin : find by ID only
                     : journalEntryRepository.findByIdAndUserId(journalId,user.getId());  // User: find by ID and owner.
              if(journalEntryOptional.isPresent()){
                  journalEntryRepository.delete(journalEntryOptional.get());

                  if (user != null) {
                      // This is a standard user deleting their own entry.
                      log.warn("User '{}' deleted their journal entry with ID: {}", user.getUsername(), journalId);
                  } else {
                      // This is an admin performing a moderation action.
                      log.warn("ADMIN ACTION: Deleted journal entry with ID: {}", journalId);
                  }
                  return true;
              }
             String username = (user != null) ? user.getUsername():"ADMIN";
             log.warn("Attempt to delete non-existent or unowned journal entry ID: {} by user: {}", journalId, username);
             return false;
         }catch (Exception e){
             String username = (user != null) ? user.getUsername() : "ADMIN";
             log.error("Error deleting journal entry {} for user/actor {}", journalId, username, e);
             throw new RuntimeException("Could not delete journal entry due to an internal error.", e);
         }
    }

    // --------------------------------------------------------------------
    // --- NEW METHODS FOR PUBLICJOURNALCONTROLLER ---
    // --------------------------------------------------------------------

    /**
     * Retrieves a paginated list of all journal entries that have been marked as public.
     * @param pageable Pagination information (page, size, sort).
     * @return A Page of public JournalResponseDTO objects.
     */

    public Page<JournalResponseDTO> getPublicEntries(Pageable pageable) {
           try{
               // 1. Fetch the page of public entities from the repository.
               Page<JournalEntry> publicJournalEntry = journalEntryRepository.findByIsPublic(true,pageable);
               // 2. Map the entities to response DTOs.
               log.info("Public journal entries fetched successfully.");
               return publicJournalEntry.map(
                       entry -> JournalMapper
                               .toResponseDTO(entry,"A User"));
           }catch (Exception e){
               log.error("Error fetching public journal entries.", e);
               throw new RuntimeException("Could not fetch public journal entries.", e);
           }
    }

    /**
     * Finds a single public journal entry by its ID.
     * @param journalId The ID of the entry.
     * @return An Optional containing the JournalResponseDTO if the entry exists and is public.
     */
    public Optional<JournalResponseDTO> findPublicEntryById(ObjectId journalId) {
         try{
             // 1. Fetch the entity from the repository, ensuring it's public.
             Optional<JournalEntry>entryOptional=journalEntryRepository.findByIdAndIsPublic(journalId,true);
             // 2. Map the entity to a response DTO.
             log.info("A public journal entries fetched successfully.");
             return entryOptional.map(entry -> JournalMapper.toResponseDTO(entry, "A User"));
         } catch (Exception e) {
             log.error("Error finding public journal entry with ID: {}", journalId, e);
             throw new RuntimeException("Could not find public journal entry.", e);
         }
    }

    /**
     * Adds a comment to a public journal entry.
     * @param journalId The ID of the public entry.
     * @param commentText The text of the comment.
     * @param user The user posting the comment.
     */
    @Transactional
    public void addComment(ObjectId journalId, String commentText, User user) {
         try{
             // 1. Find the public entry. Throws an exception if not found or not public.
             JournalEntry entry = journalEntryRepository.findByIdAndIsPublic(journalId, true)
                     .orElseThrow(() -> new RuntimeException("Public journal entry not found or is private."));

             // 2. Create and populate the new comment object.

             Comment comment = new Comment();
             comment.setUserId(user.getId());
             comment.setUsername(user.getUsername());
             comment.setText(commentText);
             comment.setCreatedAt(LocalDateTime.now());
             // 3. Add the comment to the entry's list and save.
             entry.getComments().add(comment);
             journalEntryRepository.save(entry);
             log.info("User {} added a comment to public journal entry {}", user.getUsername(), journalId);
         } catch (Exception e) {
             log.error("Error adding comment to journal entry {}:", journalId, e);
             throw e; // Re-throw the exception for the controller to handle
         }
    }


    /**
     * Adds a rating to a public journal entry and recalculates the average.
     * @param journalId The ID of the public entry.
     * @param rating The rating value (e.g., 1-5).
     * @param user The user submitting the rating.
     */
    @Transactional
    public void addRating(ObjectId journalId, int rating, User user) {

        try{
            // 1. Validate the input rating.
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }
            // 2. Find the public entry.
            JournalEntry entry = journalEntryRepository.findByIdAndIsPublic(journalId,true)
                    .orElseThrow(()->new RuntimeException("Public journal entry not found or is private."));

            // 3. Get the current stats and recalculate the new average.
            RatingStats stats = entry.getRatingStats();
            double currentTotalRating = stats.getAverageRating() * stats.getRatingCount();
            int newCount = stats.getRatingCount()+1;
            double newAverage = (currentTotalRating + rating) / newCount;

            // 4. Update the stats object.
            stats.setRatingCount(newCount);
            // Round to 2 decimal places for a clean average
            stats.setAverageRating(Math.round(newAverage * 100.0) / 100.0);

            entry.setRatingStats(stats);
            journalEntryRepository.save(entry);
            log.info("User {} rated public journal entry {} with a score of {}", user.getUsername(), journalId, rating);
        } catch (Exception e) {
            log.error("Error adding rating to journal entry {}:", journalId, e);
            throw e; // Re-throw the exception for the controller to handle
        }


    }
}
