package net.manifest.journalapp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.entity.JournalEntry;
import net.manifest.journalapp.enums.Sentiment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A dedicated service for performing rule-based sentiment analysis.
 * This version uses a weighted score from the user's detailed mood and an expanded keyword analysis.
 */

@Slf4j
@Service
public class SentimentAnalysisService {
    //Comprehensive list of positive keywords you provided.
    private static final List<String> POSITIVE_KEYWORDS = Arrays.asList(
            // Emotions & Feelings
            "happy", "joy", "joyful", "wonderful", "amazing", "awesome", "great", "fantastic",
            "success", "grateful", "love", "proud", "beautiful", "celebrate", "achieved",
            "delighted", "excited", "peaceful", "blessed", "inspired", "calm", "hopeful",
            "positive", "optimistic", "serene", "cheerful", "playful", "radiant", "victory",
            "motivated", "productive", "progress", "growth", "healthy", "fit", "strong",

            // Relationships & Social
            "compassion", "kindness", "friendship", "connected", "support", "family", "bond",
            "together", "team", "unity", "forgiveness", "trust", "helpful", "encouraged",

            // Personal Strength
            "confidence", "courage", "strength", "healing", "freedom", "resilience", "balance",
            "clarity", "mindful", "present", "thankful", "creative", "abundant", "fulfilled",
            "energized", "focused", "relaxed", "flow", "adventure", "discovery", "learning",

            // Spiritual / Devotional
            "spiritual", "faith", "prayer", "divine", "soul", "karma", "truth", "light", "radhe",
            "krishna", "govind", "gopal", "radheshyam", "harekrishna", "ram", "sita", "hanuman",
            "shiv", "shankar", "mahadev", "om", "bhakti", "devotion", "nirvana", "moksha",
            "temple", "blessing", "sacred", "eternal", "awareness", "harmony", "gratitude",

            // Achievements & Progress
            "promotion", "rewarded", "successfully", "milestone", "goal", "ambition",
            "win", "accomplished", "improvement", "breakthrough", "innovation"
    );

    // Comprehensive list of negative keywords you provided.
    private static final List<String> NEGATIVE_KEYWORDS = Arrays.asList(
            // Emotions & Feelings
            "sad", "angry", "terrible", "horrible", "awful", "bad", "upset", "disappointed",
            "failure", "hate", "anxious", "worried", "stressed", "stressful", "tired",
            "exhausted", "hopeless", "fear", "guilt", "shame", "regret", "crying", "tears",

            // Personal Struggles
            "broken", "empty", "lost", "weak", "worthless", "insecure", "angst", "hurt",
            "trauma", "suffering", "sick", "ill", "pain", "disease", "injury", "fatigue",

            // Relationships & Social
            "lonely", "jealous", "envy", "ignored", "betrayed", "abandoned", "rejected",
            "unloved", "isolation", "conflict", "argument", "fight", "toxic", "abuse",

            // Mental Health
            "frustrated", "overwhelmed", "depressed", "mourning", "grief", "dark", "numb",
            "anxiety", "panic", "paranoid", "fearful", "insecure", "ashamed",

            // Spiritual / Negative energy
            "sin", "curse", "kali", "demon", "devil", "evil", "darkness", "karma-bad",
            "unholy", "fallen", "doomed", "unforgiven"
    );

    /**
     * Analyzes a JournalEntry and returns a calculated Sentiment enum.
     * @param entry The JournalEntry to analyze (must contain mood and content).
     * @return The determined Sentiment (e.g., POSITIVE, NEGATIVE, NEUTRAL).
     */


    public Sentiment analyze(JournalEntry entry){

        if(entry.getMood() == null && (entry.getContent() == null || entry.getContent().isBlank() )){
              return  Sentiment.NEUTRAL;
        }

        // --- Step 1: Get the Mood Score (High Weight) ---
        int moodScore =0;

        if(entry.getMood() != null){
             moodScore = entry.getMood().getScore();
        }else {
            moodScore = 5; // Default to a neutral score (5 out of 10) if no mood is selected
        }

        // --- Step 2: Calculate the Keyword Score (Low Weight) ---
        int keywordScore =0;
        if(entry.getContent() != null && !entry.getContent().isBlank()){
            List<String> tokens = Arrays.stream(entry.getContent().toLowerCase().split("\\W+"))
                    .collect(Collectors.toList());

            for(String word :POSITIVE_KEYWORDS){
                 if(tokens.contains(word)){
                      keywordScore++;
                 }
            }
            for(String word : NEGATIVE_KEYWORDS){
                 if(tokens.contains(word)){
                     keywordScore--;
                 }
            }
        }

        // --- Step 3: Combine Scores and Determine Final Sentiment ---
        int finalScore = (moodScore * 4) + keywordScore;

        log.debug("Sentiment analysis for entry ID {}: MoodScore={}, KeywordScore={}, FinalScore={}",
                entry.getId(), moodScore, keywordScore, finalScore);

        // --- Step 4: Map the final score to our Sentiment enum ---
        if (finalScore >= 35) return Sentiment.VERY_POSITIVE;
        if (finalScore >= 20) return Sentiment.POSITIVE;
        if (finalScore > 15) return Sentiment.NEUTRAL;
        if (finalScore > 8) return Sentiment.NEGATIVE;
        return Sentiment.VERY_NEGATIVE;
    }

}
