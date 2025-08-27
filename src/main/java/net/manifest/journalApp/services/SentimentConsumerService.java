package net.manifest.journalApp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SentimentConsumerService {

      @Autowired
      private EmailService emailService;

      @KafkaListener(topics="weekly-sentiments",groupId = "weekly-sentiment-group")
      public void consume(SentimentData sentimentData){
          log.info("Consumed sentiment data for email: {}", sentimentData.getEmail());
          try {
              sendEmail(sentimentData);
              log.info("Successfully sent sentiment report to: {}", sentimentData.getEmail());
          } catch (Exception e) {

              log.error("Failed to send sentiment email to: {}. Error: {}", sentimentData.getEmail(), e.getMessage());
              // Optional: Here you could add logic to send the failed message
              // to a Dead Letter Topic (DLT) for later analysis or retry.
          }
      }

      private void sendEmail(SentimentData sentimentData){
          String body = String.format(
                  "Hello %s,\n\nYour sentiment for the past week was: %s\n\nRegards,\nThe Journal App Team",
                  sentimentData.getUserName(),
                  sentimentData.getSentiment()
          );
           emailService.sendEmail(sentimentData.getEmail(),
                   "Your Weekly Sentiment Analysis Report",
                    body,
                   "JournalApp");
      }
}
