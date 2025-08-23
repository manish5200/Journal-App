package net.manifest.journalApp.controller;

import net.manifest.journalApp.api.response.WeatherResponse;
import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.repository.UserRepository;
import net.manifest.journalApp.services.UserService;
import net.manifest.journalApp.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    @PutMapping
    public  ResponseEntity<?>updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String myUserName = authentication.getName();
         User userInDb = userService.findByUserName(myUserName);
               userInDb.setUserName(user.getUserName());
               userInDb.setPassword(user.getPassword());
               userService.saveNewUser(userInDb);
          return new ResponseEntity<>("User updated successfully.",HttpStatus.OK);
    }

    @DeleteMapping
    public  ResponseEntity<?>deleteUserByUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String myUserName = authentication.getName();
        userRepository.deleteByUserName(myUserName);
        return new ResponseEntity<>("User deleted successfully.",HttpStatus.NO_CONTENT);
    }


     @GetMapping("/external-api")
      public ResponseEntity<?>greetings(){
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          String  userName = authentication.getName();

         WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
         String response ="";
         if(weatherResponse != null){
             response = ", Weather feels like "+weatherResponse.getCurrent().getFeelslike();
         }
         return new ResponseEntity<>("Hi "+userName +response,HttpStatus.OK);
      }
}
