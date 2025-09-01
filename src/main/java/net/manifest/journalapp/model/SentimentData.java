package net.manifest.journalapp.model;

import lombok.*;
import net.manifest.journalapp.enums.Mood;
import net.manifest.journalapp.enums.Sentiment;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentData {
    private String email;
    private String userName;
    // --- ENHANCED FIELDS ---
    private Mood dominantMood;
    private Sentiment dominantSentiment;
    private double averageMoodScore;
    private int entryCount;
}
