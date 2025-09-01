package net.manifest.journalapp.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalapp.entity.JournalEntry;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.entity.WeeklySummary;
import net.manifest.journalapp.enums.Mood;
import net.manifest.journalapp.enums.Sentiment;
import net.manifest.journalapp.repository.JournalEntryRepository;
import net.manifest.journalapp.repository.UserRepositoryImpl;
import net.manifest.journalapp.repository.WeeklySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WeeklySummaryScheduler {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private WeeklySummaryRepository weeklySummaryRepository;

    /**
     * This method runs automatically based on the CRON schedule.
     * "0 0 8 ? * SUN" means "At 8:00 AM, every Sunday".
     * It calculates and saves a fresh analysis of the previous week's activity for every user.
     */
    @Scheduled(cron = "0 0 8 ? * SUN")
    public void generateWeeklySummaries(){
         log.info("Starting weekly summary generation job...");
        // We call your custom, efficient method to get only the users who are
        // eligible for the sentiment analysis report.
        List<User> users = userRepositoryImpl.getUserForSA();
        log.info("Found {} users eligible for weekly summary.", users.size());

        for(User user :users){
             try{
                 // Set the end date to the most recent Sunday at 00:00 (start of Sunday)
                 LocalDateTime endDate = LocalDateTime.now()
                         .with(DayOfWeek.SUNDAY)
                         .withHour(0)
                         .withMinute(0);
                 // Set the start date to exactly one week before the end date
                 LocalDateTime startDate = endDate.minusWeeks(1);

                 List<JournalEntry> recentEntries = journalEntryRepository.findByUserIdAndCreatedAtAfter(user.getId(), startDate);

                 if (recentEntries.isEmpty()) {
                     log.info("No entries found for user {} in the last week. Skipping summary.", user.getUsername());
                     continue;
                 }

                 // Calculate all the summary metrics using the simplified helper methods.
                 double averageMoodScore = calculateAverageMood(recentEntries);
                 Mood dominantMood = findDominantMood(recentEntries);
                 double averageSentimentScore = calculateAverageSentiment(recentEntries);
                 Sentiment dominantSentiment = findDominantSentiment(recentEntries);
                 int entryCount = recentEntries.size();

                 WeeklySummary summary = weeklySummaryRepository.findByUserId(user.getId())
                         .orElse(new WeeklySummary());

                 summary.setUserId(user.getId());
                 summary.setAverageMoodScore(averageMoodScore);
                 summary.setDominantMood(dominantMood);
                 summary.setAverageSentimentScore(averageSentimentScore);
                 summary.setDominantSentiment(dominantSentiment);
                 summary.setEntryCount(entryCount);
                 summary.setLastCalculated(LocalDateTime.now());

                 weeklySummaryRepository.save(summary);
                 log.info("Successfully generated weekly summary for user: {}", user.getUsername());

             }catch (Exception e) {
                 log.error("Failed to generate weekly summary for user: {}", user.getUsername(), e);
             }
        }
        log.info("Weekly summary generation job finished.");

    }

    // --- SIMPLIFIED HELPER METHODS (NOT ONE-LINERS) ---

    private double calculateAverageMood(List<JournalEntry> entries) {
        double totalScore = 0.0;
        int moodCount = 0;
        for (JournalEntry entry : entries) {
            if (entry.getMood() != null) {
                totalScore += entry.getMood().getScore();
                moodCount++;
            }
        }
        return (moodCount == 0) ? 0.0 : totalScore / moodCount;
    }

    private Mood findDominantMood(List<JournalEntry> entries) {
         HashMap<Mood,Integer>moodCount = new HashMap<>();
         for(JournalEntry entry : entries){
               if(moodCount.containsKey(entry.getMood())){
                    moodCount.put(entry.getMood(),moodCount.get(entry.getMood())+1);
               }else{
                   moodCount.put(entry.getMood(),1);
               }
         }
         if(moodCount.isEmpty()) return Mood.MEH;
         Mood dominatedMood =null;
         int maxCount =-1;
         for(Map.Entry<Mood,Integer>entry : moodCount.entrySet()){
                if(entry.getValue()>maxCount){
                    maxCount = entry.getValue();
                    dominatedMood = entry.getKey();
                }
         }
          return  dominatedMood;
    }

    private double calculateAverageSentiment(List<JournalEntry> entries) {
           double totalScore =0.0;
           int sentimentCount=0;
           for(JournalEntry journalEntry : entries){
                 if(journalEntry.getSentiment() != null){
                      totalScore+=journalEntry.getSentiment().getScore();
                      sentimentCount++;
                 }
           }
           return (sentimentCount==0)?0.0:totalScore/sentimentCount;
    }

    private Sentiment findDominantSentiment(List<JournalEntry> entries) {
        Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
        for(JournalEntry entry : entries){
            if(sentimentCounts.containsKey(entry.getSentiment())){
                sentimentCounts.put(entry.getSentiment(), sentimentCounts.get(entry.getSentiment())+1);
            }else{
                sentimentCounts.put(entry.getSentiment(),1);
            }
        }
        if(sentimentCounts.isEmpty()) return Sentiment.NEUTRAL;
        Sentiment dominateSentiment =null;
        int maxCount =-1;
        for(Map.Entry<Sentiment,Integer>entry : sentimentCounts.entrySet()){
            if(entry.getValue()>maxCount){
                maxCount = entry.getValue();
                dominateSentiment = entry.getKey();
            }
        }
        return dominateSentiment;
    }
}
