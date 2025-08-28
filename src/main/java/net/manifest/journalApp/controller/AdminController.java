package net.manifest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.manifest.journalApp.cache.AppCache;
import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name="Admin APIs",description = "Get All Users , Admin-SignUp & cleaning app-cache" )
public class AdminController {
          @Autowired
          private UserService userService;
          @Autowired
          private AppCache appCache;

          @GetMapping("/users")
          public ResponseEntity<?>getAllUsers(){
                List<User> users = userService.getAllUsers();

                if(users != null && !users.isEmpty()){
                    return new  ResponseEntity<>(users,HttpStatus.OK);
                }
                return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
          }

          @PostMapping("/admin-signup")
          public ResponseEntity<?> registerNewAdmin(@RequestBody User newUser){
                 try{
                     if(userService.findByUserName(newUser.getUserName()) != null){
                         return new ResponseEntity<>("Username is already taken.",HttpStatus.CONFLICT);
                     }
                     userService.saveNewAdmin(newUser);
                     return  new ResponseEntity<>("Admin registration successful. -->  " + newUser,HttpStatus.CREATED);

                 } catch (Exception e) {
                     System.out.println(e);
                     return new ResponseEntity<>("An error occurred during registration.",HttpStatus.INTERNAL_SERVER_ERROR);
                 }
          }

          //Re-Initialising my map
          @GetMapping("/clear-app-cache")
          public ResponseEntity<?>clearAppCache(){
               appCache.init();
               return new ResponseEntity<>("App-Cache cleared successfully.",HttpStatus.OK);
          }
}
