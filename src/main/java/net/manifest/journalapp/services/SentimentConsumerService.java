package net.manifest.journalapp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SentimentConsumerService {

      @Autowired
      private SentimentNotificationService sentimentNotificationService;

    /**
     * Consumes messages from the "weekly-sentiments" Kafka topic.
     * This method is triggered automatically whenever a new message is produced
     * by the MyScheduler.
     *
     * @param sentimentData The deserialized message from Kafka, containing all the
     * insights needed to send a personalized email.
     */

      @KafkaListener(topics="weekly-sentiments",groupId = "weekly-sentiment-group")
      public void consume(SentimentData sentimentData){
          log.info("Consumed sentiment data for email: {}", sentimentData.getEmail());
          try {
              // Delegate the actual work of sending the email to the notification service.
              // This keeps the consumer's responsibility clean and focused.
              sentimentNotificationService.sendSentimentReport(sentimentData); // Simplified call
              log.info("Successfully sent sentiment report to: {}", sentimentData.getEmail());
          } catch (Exception e) {
              log.error("Failed to send sentiment email to: {}. Error: {}", sentimentData.getEmail(), e.getMessage());
          }
      }
}
