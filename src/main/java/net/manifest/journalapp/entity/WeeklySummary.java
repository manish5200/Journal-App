package net.manifest.journalapp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Stores the aggregated results of a user's weekly journaling activity.
 * This data is calculated by a scheduled job and used to send personalized summary emails.
 */
@Document(collection = "weekly_summaries")
@Data
@NoArgsConstructor
public class WeeklySummary {

    @Id
    private ObjectId id;

    @Indexed
    private ObjectId userId;


     //The average mood score, calculated from the user's self-reported moods (e.g., 1-5).

    private double averageMoodScore;


     //The most frequently reported mood for the week (e.g., "GOOD", "MEH").

    private String dominantMood;


     //The total number of journal entries the user created during the week.
    private int entryCount;


     // The date and time this summary was last calculated.

    private LocalDateTime lastCalculated;

}

