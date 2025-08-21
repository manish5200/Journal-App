package net.manifest.journalApp.cache;

import jakarta.annotation.PostConstruct;
import net.manifest.journalApp.entity.ConfigJournalAppEntity;
import net.manifest.journalApp.repository.ConfigJournalAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum keys{
         WEATHER_API;
    }


    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String,String> appCache;

       @PostConstruct
       public void init(){
            appCache = new HashMap<>();
            List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();

            if(!all.isEmpty()){
            for(ConfigJournalAppEntity cja : all){
                appCache.put(cja.getKey(),cja.getValue());
            }
        }
       }
}
