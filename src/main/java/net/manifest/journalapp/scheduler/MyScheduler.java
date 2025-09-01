package net.manifest.journalapp.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.cache.AppCache;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.model.SentimentData;
import net.manifest.journalapp.repository.UserRepositoryImpl;
import net.manifest.journalapp.repository.WeeklySummaryRepository;
import net.manifest.journalapp.services.SentimentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
;


@Slf4j
@Component
public class MyScheduler {

     @Autowired
     private UserRepositoryImpl userRepositoryImpl;
     @Autowired
     private WeeklySummaryRepository weeklySummaryRepository;
     @Autowired
     private AppCache appCache;
     @Autowired
     private KafkaTemplate<String, SentimentData>kafkaTemplate;
     @Autowired
     private SentimentNotificationService sentimentNotificationService;

     //(cron = "SEC MIN HOUR DAYOFTHEMONTH MONTH DAYOFTHEWEEK")
     @Scheduled(cron = "0 0 9 * * SUN")
     public void fetchUsersAndSendSaMail(){ // Sa -> SentimentAnalysis
        log.info("Starting weekly NOTIFICATION job.");
        List<User> userList = userRepositoryImpl.getUserForSA();
        log.info("Found {} users eligible for weekly notification.", userList.size());
        for(User user:userList){
              try{
                  // 1. Fetch the PRE-CALCULATED summary. This is a single, fast query.
                  // We use .ifPresent() for clean,
                  //safe handling if a summary for some reason doesn't exist.

                  weeklySummaryRepository.findByUserId(user.getId()).ifPresent(summary ->{
                      // 2. Build the enhanced SentimentData object from the summary.
                      SentimentData sentimentData = SentimentData.builder()
                              .email(user.getEmail())
                              .userName(user.getUsername())
                              .dominantMood(summary.getDominantMood())
                              .dominantSentiment(summary.getDominantSentiment())
                              .averageMoodScore(summary.getAverageMoodScore())
                              .entryCount(summary.getEntryCount())
                              .build();

                      log.info("Producing summary notification for email: {}", sentimentData.getEmail());
                      try{
                          kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                      } catch (KafkaException e) {
                          log.warn("Kafka producer failed. Falling back to direct email for {}. Reason: {}", sentimentData.getEmail(), e.getMessage());
                          sentimentNotificationService.sendSentimentReport(sentimentData); // Simplified call
                      }
                  });
              }catch (Exception e) {
                  log.error("Failed to process notification for user {}", user.getUsername(), e);
              }
        }
        log.info("Weekly NOTIFICATION job finished.");
    }


    @Scheduled(cron = "0 0/10 * ? * *")
    public  void clearAppCache(){
        appCache.init();
    }

}
