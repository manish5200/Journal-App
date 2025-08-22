package net.manifest.journalApp.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalApp.cache.AppCache;
import net.manifest.journalApp.entity.JournalEntry;
import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.enums.Sentiment;
import net.manifest.journalApp.repository.UserRepositoryImpl;
import net.manifest.journalApp.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
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
     private AppCache appCache;


     //(cron = "SEC MIN HOUR DAYOFTHEMONTH MONTH DAYOFTHEWEEK")

    @Scheduled(cron = "0 0 9 * * SUN")
     public void fetchUsersAndSendSaMail(){ // Sa -> SentimentAnalysis
          log.info("Starting weekly sentiment analysis job.");
          List<User> userList = userRepositoryImpl.getUserForSA();
          for(User user:userList){
             try{
                 List<JournalEntry> journalEntries =  user.getJournalEntries();
                 if(journalEntries != null && !journalEntries.isEmpty()){

                     List<Sentiment>sentiments = journalEntries.stream()
                             .filter(entry -> entry.getDate()
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
                         log.info("Sending sentiment email to user: {}", user.getUserName());
                         emailService.sendEmail(user.getEmail(),
                                 "Your Weekly Sentiment Analysis Report",
                                 "Hello " + user.getUserName() + ",\n\nYour sentiment for the past week was: " + mostFrequentSentiment.toString() + "\n\nRegards,\nThe Journal App Team",
                                 "Journal App");
                     }
                 }
             } catch (Exception e) {
                 log.error("Failed to process sentiment analysis for user {}", user.getUserName(), e);
                 // The loop will continue to the next user
             }
          }
        log.info("Weekly sentiment analysis job finished.");
    }


    @Scheduled(cron = "0 0/10 * ? * *")
    public  void clearAppCache(){
        appCache.init();
    }

}
