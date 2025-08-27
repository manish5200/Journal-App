package net.manifest.journalApp.services;

import net.manifest.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SentimentNotificationService {

    @Autowired
    private  EmailService emailService;

    public void sendSentimentReport(SentimentData sentimentData) {
        String body = String.format(
                "Hello %s,\n\nYour sentiment for the past week was: %s\n\nRegards,\nThe Journal App Team",
                sentimentData.getUserName(),
                sentimentData.getSentiment()
        );

        emailService.sendEmail(
                sentimentData.getEmail(),
                "Your Weekly Sentiment Analysis Report",
                body,
                "JournalApp"
        );
    }
}
