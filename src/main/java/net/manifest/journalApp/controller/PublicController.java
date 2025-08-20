package net.manifest.journalApp.controller;


import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String>userEntry(@RequestBody User newUser) {

//        try{
//            if(userService.findByUserName(newUser.getUserName()) != null){
//                  return new ResponseEntity<>("Username is already taken.",HttpStatus.CONFLICT);
//            }
//            userService.saveNewUser(newUser);
//            return new ResponseEntity<>("Registration Successful.", HttpStatus.CREATED);
//        }catch (Exception e){
//             return new ResponseEntity<>("An error occurred during registration.",HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        userService.saveNewUser(newUser);
        return new ResponseEntity<>("Registration Successful.", HttpStatus.CREATED);
    }

    @GetMapping("/health-check")
        public ResponseEntity<?> healthCheck(){
        return  new ResponseEntity<>("Hey Manish..., My health is best. Thank you for asking :)",HttpStatus.OK);
    }
}
