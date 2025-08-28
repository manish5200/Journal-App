package net.manifest.journalApp.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.manifest.journalApp.dto.UserDTO;
import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.services.UserDetailsServiceImpl;
import net.manifest.journalApp.services.UserService;
import net.manifest.journalApp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
@Tag(name="Public APIs",description = "User Signup,Login & health-check" )
public class PublicController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<String>signup(@RequestBody UserDTO user) {

//        try{
//            if(userService.findByUserName(newUser.getUserName()) != null){
//                  return new ResponseEntity<>("Username is already taken.",HttpStatus.CONFLICT);
//            }
//            userService.saveNewUser(newUser);
//            return new ResponseEntity<>("Registration Successful.", HttpStatus.CREATED);
//        }catch (Exception e){
//             return new ResponseEntity<>("An error occurred during registration.",HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setUserName(user.getUserName());
        newUser.setPassword(user.getPassword());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());
        userService.saveNewUser(newUser);
        return new ResponseEntity<>("Registration Successful.", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String>login(@RequestBody User user) {
         try{
             authenticationManager.authenticate(
                     new UsernamePasswordAuthenticationToken(
                             user.getUserName(),
                             user.getPassword())
             );

             UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
             String jwt = jwtUtils.generateToken(userDetails.getUsername());
             return new ResponseEntity<>("Login Successful: "
                     + jwt,
                     HttpStatus.OK);

         }catch (Exception e){
             log.error("Exception occurred while creating AuthenticationToken: "+"Login Method",e);
             return new ResponseEntity<>("Incorrect username or password.",HttpStatus.BAD_REQUEST);
         }
    }

    @GetMapping("/health-check")
        public ResponseEntity<?> healthCheck(){
        return  new ResponseEntity<>("Hey Manish..., My health is best. Thank you for asking :)",HttpStatus.OK);
    }
}
