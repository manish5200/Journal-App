package net.manifest.journalapp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.manifest.journalapp.enums.Mood;
import net.manifest.journalapp.enums.Sentiment;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Stores the aggregated and pre-calculated results of a user's weekly journaling activity.
 * This collection is populated by a scheduled background job (WeeklySummaryScheduler)
 * and serves as a fast data source for features like personalized summary emails.
 */
@Document(collection = "weekly_summaries")
@Data
@NoArgsConstructor
public class WeeklySummary {

    @Id
    private ObjectId id;

    @Indexed
    private ObjectId userId;

    // --- Mood-Based Metrics ---
    private double averageMoodScore;

    /**
     * The most frequently reported mood for the week. Storing the enum type ensures data integrity.
     */
    private Mood dominantMood;

    // --- Sentiment-Based Metrics ---
    private double averageSentimentScore;

    /**
     * The most frequently analyzed sentiment for the week. Storing the enum type ensures data integrity.
     */
    private Sentiment dominantSentiment;

    // --- General Metrics ---
    private int entryCount;
    private LocalDateTime lastCalculated;
}

