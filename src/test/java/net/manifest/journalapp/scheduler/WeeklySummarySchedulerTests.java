package net.manifest.journalapp.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeeklySummarySchedulerTests {
     @Autowired
     private WeeklySummaryScheduler weeklySummaryScheduler;
     @Test
     public void testWeeklyScheduler(){
          weeklySummaryScheduler.generateWeeklySummaries();
     }
}
