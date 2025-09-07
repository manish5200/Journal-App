package net.manifest.journalapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
// Add this annotation to enable stable page serialization
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class JournalApplication {

    public static void main(String[] args) {

       ConfigurableApplicationContext context = SpringApplication.run(JournalApplication.class, args);
       ConfigurableEnvironment appEnv = context.getEnvironment();
       log.info("Currently active profile: {}",appEnv.getActiveProfiles()[0]);
    }
    @Bean
     public PlatformTransactionManager mongoTransactionBean(MongoDatabaseFactory dbFactory){
           return  new MongoTransactionManager(dbFactory);
     }

}