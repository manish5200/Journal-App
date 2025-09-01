package net.manifest.journalapp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service responsible for creating and sending personalized weekly summary emails.
 * It uses the rich data from the WeeklySummary to generate empathetic and insightful content.
 */
@Slf4j
@Service
public class SentimentNotificationService {

    @Autowired
    private EmailService emailService;

    /**
     * The main method called by the Kafka consumer. It orchestrates the creation
     * and sending of the weekly sentiment report email.
     *
     * @param data The rich SentimentData object consumed from Kafka.
     */
    public void sendSentimentReport(SentimentData data) {
        try {
            // 1. Create a personalized, intelligent email body based on the user's data.
            String subject = "Your Weekly Journal Reflection is Here!";
            String body = createPersonalizedBody(data);

            // 2. Send the email using the generic EmailService.
            emailService.sendEmail(
                    data.getEmail(),
                    subject,
                    body,
                    "JournalApp"
            );
            log.info("Successfully sent sentiment report to: {}", data.getEmail());
        } catch (Exception e) {
            log.error("Failed to send sentiment email to: {}. Error: {}", data.getEmail(), e.getMessage());
            // It's important to handle potential errors from the email sending service.
        }
    }

    /**
     * A "rules engine" that creates a personalized email body based on the user's
     * weekly summary data. This is where the application's emotional intelligence lives.
     *
     * @param data The user's weekly summary data.
     * @return A formatted, empathetic email body string.
     */
    private String createPersonalizedBody(SentimentData data) {
        String userName = data.getUserName();
        String dominantMoodName = data.getDominantMood().name().toLowerCase();
        String dominantMoodEmoji = data.getDominantMood().getEmoji();
        String dominantSentimentName = data.getDominantSentiment().name().toLowerCase();
        int entryCount = data.getEntryCount();

        // Rule 1: "Aligned & Positive" Week - User felt good and wrote positively.
        if (data.getDominantMood().getScore() >= 7 && data.getDominantSentiment().getScore() >= 1) {
            return String.format(
                    "Hello %s,\n\nIt looks like you had a fantastic week! Your most frequent mood was %s %s, " +
                            "and your writing was overwhelmingly %s. It's wonderful when your feelings and your words are in harmony.\n\n" +
                            "You wrote %d entries this week. Keep up the great journaling habit!\n\n" +
                            "Regards,\nThe Journal App Team",
                    userName, dominantMoodName, dominantMoodEmoji, dominantSentimentName, entryCount
            );
        }

        // Rule 2: "Finding the Good" Week - User felt bad, but wrote positively (a sign of resilience).
        if (data.getDominantMood().getScore() <= 4 && data.getDominantSentiment().getScore() >= 1) {
            return String.format(
                    "Hello %s,\n\nThis week's reflection is really insightful. Even though your most frequent mood was %s %s, " +
                            "your writing was consistently %s. This suggests you are actively practicing gratitude or finding the silver lining in difficult situations. " +
                            "That's an incredible sign of resilience.\n\n" +
                            "You wrote %d entries this week. Well done for journaling through it.\n\n" +
                            "Regards,\nThe Journal App Team",
                    userName, dominantMoodName, dominantMoodEmoji, dominantSentimentName, entryCount
            );
        }

        // Rule 3: "Vent & Release" Week - User felt bad and wrote negatively.
        if (data.getDominantMood().getScore() <= 4 && data.getDominantSentiment().getScore() <= -1) {
            return String.format(
                    "Hello %s,\n\nThis is a gentle check-in. It seems like this past week may have been challenging. " +
                            "Your most frequent mood was %s %s, and your writing reflects that you were processing some very %s feelings. " +
                            "This is a healthy and powerful way to use your journal—as a safe space to vent and release.\n\n" +
                            "Be kind to yourself. A simple prompt if you need it: 'What is one small thing that could bring you a moment of comfort today?'\n\n" +
                            "Regards,\nThe Journal App Team",
                    userName, dominantMoodName, dominantMoodEmoji, dominantSentimentName
            );
        }

        // Default Fallback Email - For all other balanced or neutral weeks.
        return String.format(
                "Hello %s,\n\nHere is your weekly journal reflection. Your most frequent mood was %s %s, " +
                        "and you wrote %d entries this week. Taking the time to reflect is a wonderful habit.\n\n" +
                        "Regards,\nThe Journal App Team",
                userName, dominantMoodName, dominantMoodEmoji, entryCount
        );
    }
}
