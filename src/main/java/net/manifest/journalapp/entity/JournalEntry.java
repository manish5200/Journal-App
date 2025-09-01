package net.manifest.journalapp.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.manifest.journalapp.enums.Mood;
import net.manifest.journalapp.enums.Sentiment;
import net.manifest.journalapp.utils.journalutils.Comment;
import net.manifest.journalapp.utils.journalutils.Location;
import net.manifest.journalapp.utils.journalutils.RatingStats;
import net.manifest.journalapp.utils.journalutils.Weather;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "journal_entries")
@Data
@NoArgsConstructor
public class JournalEntry {

    @Id
    private ObjectId id;

    /**
     * The ID of the User who owns this entry. This is the most critical index for data retrieval,
     * ensuring fast lookups of a user's entries.
     */
    @NonNull
    @Indexed
    private ObjectId userId;

    @NotBlank(message = "Title is required.")
    private String title;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // --- Metadata (User-Provided & App-Generated) ---

    //The user's self-reported mood at the time of writing.
    private Mood mood;

    //The sentiment of the 'content' text, as analyzed by the application.
    //This is only populated if the user has `sentimentAnalysisEnabled` set to true.
    private Sentiment sentiment;

    /**
     * A list of user-defined tags for categorizing the entry. Indexed for fast filtering.
     * Examples: ["work", "gratitude", "travel", "project-alpha", "family-event"]
     */
    @Indexed
    private List<String> tags = new ArrayList<>();

    // --- Sharing & Interaction ---
    private boolean isPublic = false;
    private RatingStats ratingStats = new RatingStats();
    private List<Comment> comments = new ArrayList<>();

    // --- Rich Context (Optional) ---
    private Location location;
    private Weather weather;

}
