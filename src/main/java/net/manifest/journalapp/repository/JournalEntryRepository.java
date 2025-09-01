package net.manifest.journalapp.repository;

import net.manifest.journalapp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {

    List<JournalEntry>findByUserId(ObjectId userId);
    void deleteAllByUserId(ObjectId userId);
    Optional<JournalEntry> findByIdAndUserId(ObjectId myId, ObjectId id);
    boolean existsByIdAndUserId(ObjectId journalId, ObjectId userId);
    Page<JournalEntry>findByUserId(ObjectId userId, Pageable pageable);
    Page<JournalEntry> findByIsPublic(boolean isPublic, Pageable pageable);

    Optional<JournalEntry> findByIdAndIsPublic(ObjectId journalId, boolean isPublic);

    // --- NEW METHOD FOR TAG FILTERING ---

    /**
     * Finds a paginated list of all journal entries for a specific user
     * where the 'tags' array field contains the given tag string.
     *
     * @param userId The ID of the user.
     * @param tag The tag string to search for.
     * @param pageable Pagination information.
     * @return A Page of matching JournalEntry entities.
     */

     Page<JournalEntry>findByUserIdAndTagsContains(ObjectId userId,String tag,Pageable pageable);

    // --- NEW METHOD FOR PUBLIC TAG FILTERING ---

    /**
     * Finds a paginated list of all journal entries that are marked as public
     * AND where the 'tags' array field contains the given tag string.
     *
     * @param isPublic Must be 'true' for this query.
     * @param tag The tag string to search for.
     * @param pageable Pagination information.
     * @return A Page of matching public JournalEntry entities.
     */
    Page<JournalEntry> findByIsPublicAndTagsContains(boolean isPublic, String tag, Pageable pageable);
    List<JournalEntry> findByUserIdAndCreatedAtAfter(ObjectId userId, LocalDateTime createdAt);
}
