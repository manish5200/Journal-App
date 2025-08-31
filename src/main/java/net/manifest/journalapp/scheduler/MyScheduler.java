package net.manifest.journalapp.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.cache.AppCache;
import net.manifest.journalapp.entity.JournalEntry;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.enums.Sentiment;
import net.manifest.journalapp.model.SentimentData;
import net.manifest.journalapp.repository.JournalEntryRepository;
import net.manifest.journalapp.repository.UserRepositoryImpl;
import net.manifest.journalapp.services.EmailService;
import net.manifest.journalapp.services.SentimentNotificationService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public class MyScheduler {

     @Autowired
     private EmailService emailService;
     @Autowired
     private UserRepositoryImpl userRepositoryImpl;
     @Autowired
     private JournalEntryRepository journalEntryRepository;
     @Autowired
     private AppCache appCache;
     @Autowired
     private KafkaTemplate<String, SentimentData>kafkaTemplate;
     @Autowired
     private SentimentNotificationService sentimentNotificationService;

     //(cron = "SEC MIN HOUR DAYOFTHEMONTH MONTH DAYOFTHEWEEK")
    @Scheduled(cron = "0 0 9 * * SUN")
     public void fetchUsersAndSendSaMail(){ // Sa -> SentimentAnalysis
          log.info("Starting weekly sentiment analysis job.");
          List<User> userList = userRepositoryImpl.getUserForSA();
          for(User user:userList){
             try{
                 ObjectId id = user.getId();
                 List<JournalEntry> journalEntries = journalEntryRepository.findByUserId(id);
                 if(journalEntries != null && !journalEntries.isEmpty()){

                     List<Sentiment>sentiments = journalEntries.stream()
                             .filter(entry -> entry.getCreatedAt()
                                     .isAfter(LocalDateTime.now().minus(7,ChronoUnit.DAYS)))
                             .map(x -> x.getSentiment())
                             .collect(Collectors.toList());

                     Map<Sentiment,Integer> sentimentsCount = new HashMap<>();
                     for(Sentiment sentiment :sentiments){
                          if(sentiment != null)
                              sentimentsCount.put(sentiment,sentimentsCount.getOrDefault(sentiment,0)+1);
                     }
                     Sentiment mostFrequentSentiment = null;
                     int maxCount=0;
                     for(Map.Entry<Sentiment,Integer> entry : sentimentsCount.entrySet()){
                          if(entry.getValue()>maxCount){
                               maxCount =entry.getValue();
                               mostFrequentSentiment = entry.getKey();
                          }
                     }

                     if(mostFrequentSentiment != null) {
                         SentimentData sentimentData = SentimentData
                                 .builder()
                                 .email(user.getEmail())
                                 .userName(user.getUsername())
                                 .sentiment(mostFrequentSentiment.toString())
                                 .build();

                         log.info("Producing sentiment data for email: {}", sentimentData.getEmail());

                         try{
                             kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                         } catch (KafkaException e) {
                             log.warn("Kafka producer failed. Falling back to direct email for {}. Reason: {}", sentimentData.getEmail(), e.getMessage());
                             sentimentNotificationService.sendSentimentReport(sentimentData); // Simplified call
                         }

                     }
                 }
             } catch (Exception e) {
                 // This catch block now correctly handles errors in fetching/processing user data,
                 // without interfering with the Kafka logic.
                 log.error("Failed to process sentiment analysis for user {}", user.getUsername(), e);
             }
          }
        log.info("Weekly sentiment analysis job finished.");
    }


    @Scheduled(cron = "0 0/10 * ? * *")
    public  void clearAppCache(){
        appCache.init();
    }

}
