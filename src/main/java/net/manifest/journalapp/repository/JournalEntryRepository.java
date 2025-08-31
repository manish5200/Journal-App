package net.manifest.journalapp.repository;

import net.manifest.journalapp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

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
}
