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
      private SentimentNotificationService sentimentNotificationService;

      @KafkaListener(topics="weekly-sentiments",groupId = "weekly-sentiment-group")
      public void consume(SentimentData sentimentData){
          log.info("Consumed sentiment data for email: {}", sentimentData.getEmail());
          try {
              sentimentNotificationService.sendSentimentReport(sentimentData); // Simplified call
              log.info("Successfully sent sentiment report to: {}", sentimentData.getEmail());
          } catch (Exception e) {
              log.error("Failed to send sentiment email to: {}. Error: {}", sentimentData.getEmail(), e.getMessage());
          }
      }
}
