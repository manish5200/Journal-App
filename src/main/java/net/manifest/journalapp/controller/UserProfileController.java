package net.manifest.journalapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.manifest.journalapp.api.response.WeatherResponse;
import net.manifest.journalapp.dto.auth.ChangePasswordDTO;
import net.manifest.journalapp.dto.auth.UserUpdateDTO;
import net.manifest.journalapp.entity.User;
import net.manifest.journalapp.services.UserService;
import net.manifest.journalapp.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@Tag(name = "User Profile APIs", description = "Endpoints for managing the authenticated user's profile")
//@SecurityRequirement(name = "Bearer Authentication")
public class UserProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private WeatherService weatherService;

    //Extract username
    public String authenticatedUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    private User getCurrentUserFromDatabase() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database: " + username));
    }

    //GET CURRENT LOGGED_IN USER
    @Operation(summary = "Get current logged-in user's profile details")
    @GetMapping
    public ResponseEntity<User> getMyProfile() {
        User loggedInUser = getCurrentUserFromDatabase();
        return ResponseEntity.status(HttpStatus.OK).body(loggedInUser);
    }


    //UPDATE USER'S PROFILE

    @PutMapping
    @Operation(summary = "Update user's profile details")
    public  ResponseEntity<?>updateUserProfile(@RequestBody UserUpdateDTO newUser){
        String username = getCurrentUserFromDatabase().getUsername();
        User updatedUser = userService.updateUserProfile(username, newUser);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }


    //CHANGE PASSWORD

    @PostMapping("/change-password")
    @Operation(summary = "Change user's password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO){
         String username = getCurrentUserFromDatabase().getUsername();
         userService.changePassword(username,changePasswordDTO);
         return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully☑️.");
    }



    //ExternalAPI
    @GetMapping("/external-api")
     @Operation(summary = "External Weather API - To learn how to hit external APIs")
      public ResponseEntity<String>greetings(){
          String  userName = getCurrentUserFromDatabase().getUsername();
         WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
         String response ="";
         if(weatherResponse != null){
             response = ", Weather feels like "+weatherResponse.getCurrent().getFeelslike();
         }
         return new ResponseEntity<>("Hi "+userName +response,HttpStatus.OK);
      }
}
