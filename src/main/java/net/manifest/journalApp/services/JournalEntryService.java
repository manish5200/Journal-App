package net.manifest.journalApp.services;

import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.repository.*;
import net.manifest.journalApp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry myEntry, String userName) {
        try {
            User user = userService.findByUserName(userName);
            myEntry.setDate(LocalDate.now());
            JournalEntry saved = journalEntryRepository.save(myEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        }catch(Exception e){
             System.out.println(e);
        }
    }

    @Transactional
    public void saveEntry(JournalEntry myEntry) {
        try {
            journalEntryRepository.save(myEntry);
        }catch(Exception e){
            System.out.println(e);
        }
    }


    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }


    public Optional<JournalEntry> findById(ObjectId myId){
        return journalEntryRepository.findById(myId);
    }


    public boolean deleteEntryById(ObjectId myId, String userName){
         boolean removed = false;
        try{
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x ->x.getId().equals(myId));
            if(removed){
                userService.saveUser(user);
                journalEntryRepository.deleteById(myId);
            }
        }catch(Exception e){
              System.out.println(e);
              throw new RuntimeException("An error occurred while deleting the journal entry.",e);
        }

        return removed;
    }

}
