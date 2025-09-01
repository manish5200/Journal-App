package net.manifest.journalapp.repository;

import net.manifest.journalapp.entity.WeeklySummary;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WeeklySummaryRepository extends MongoRepository<WeeklySummary, ObjectId> {

    /**
     * Finds a weekly summary by the ID of the user it belongs to.
     * This is the primary method used by the scheduler to find an existing summary
     * to update. It returns an Optional because a new user may not have a summary yet.
     *
     * @param userId The ObjectId of the user.
     * @return An {@link Optional} containing the {@link WeeklySummary} if found, otherwise empty.
     */
    Optional<WeeklySummary> findByUserId(ObjectId userId);

    /**
     * Deletes a weekly summary by the ID of the user it belongs to.
     * This is a crucial method for data cleanup, called by the UserService when an
     * admin deletes a user account to prevent orphaned summary documents.
     * @param userId The ObjectId of the user whose summary should be deleted.
     */
    void deleteByUserId(ObjectId userId);
}
