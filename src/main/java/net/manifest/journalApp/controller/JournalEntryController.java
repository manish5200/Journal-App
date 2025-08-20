package net.manifest.journalApp.controller;

import net.manifest.journalApp.entity.JournalEntry;
import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.services.JournalEntryService;
import net.manifest.journalApp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?>createEntry(@RequestBody JournalEntry myEntry) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveEntry(myEntry,userName);
            return new ResponseEntity<>("Journal entry created successfully."+myEntry, HttpStatus.CREATED);
        }catch(Exception e){
             return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?>getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user  = userService.findByUserName(userName);
        List<JournalEntry> all = user.getJournalEntries();
         if(all != null && !all.isEmpty()){
              return  ResponseEntity.ok(all);
         }else{
             return  new ResponseEntity<>("No Journal Entry found for the user.",HttpStatus.NOT_FOUND);
         }
    }

    @GetMapping("Id/{myId}")
    public ResponseEntity<JournalEntry>getJournalById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user =  userService.findByUserName(userName);
          List<JournalEntry> collect =  user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
          if(!collect.isEmpty()){
              Optional<JournalEntry>journalEntry = journalEntryService.findById(myId);
              if(journalEntry.isPresent()){
                  return  ResponseEntity.ok(journalEntry.get());
              }

          }
              return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("Id/{myId}")
    public ResponseEntity<?>deleteEntryById(@PathVariable ObjectId myId) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String userName = authentication.getName();
         boolean removed = journalEntryService.deleteEntryById(myId,userName);
         if(removed) {
             return new ResponseEntity<>("Journal entry deleted successfully.", HttpStatus.NO_CONTENT);
         }else {
              return  new ResponseEntity<>("No journal entry to delete for this id.",HttpStatus.NOT_FOUND);
         }
    }

    @PutMapping("Id/{myId}")
    public ResponseEntity<?>updateEntryById(@RequestBody JournalEntry newEntry,
                                        @PathVariable ObjectId myId) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
         User user = userService.findByUserName(username);
         List<JournalEntry> collect = user.getJournalEntries().stream().filter(x->x.getId().equals(myId)).collect(Collectors.toList());
         if(!collect.isEmpty()){
             Optional<JournalEntry>journalEntry = journalEntryService.findById(myId);
             if(journalEntry.isPresent()) {
                     JournalEntry old = journalEntry.get();
                     old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : old.getTitle());
                     old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : old.getContent());
                     journalEntryService.saveEntry(old);
                     return new ResponseEntity<>("Journal entry having ObjectId : "+myId+" has been updated successfully.  :->  "+old, HttpStatus.OK);
             }
         }

           return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
