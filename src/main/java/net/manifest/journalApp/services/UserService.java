package net.manifest.journalApp.services;

import lombok.extern.slf4j.Slf4j;
import net.manifest.journalApp.entity.User;
import net.manifest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

        @Autowired
        private UserRepository userRepository;

        private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
       // private static final Logger logger = LoggerFactory.getLogger(UserService.class);

        public void saveNewUser(User newUser){
            try{
                String hashedPswd = passwordEncoder.encode(newUser.getPassword());
                newUser.setPassword(hashedPswd);
                newUser.setRoles(Arrays.asList("USER"));
                userRepository.save(newUser);
            } catch (Exception e) {
                //throw new RuntimeException(e);
                log.error("having error in {} {} : ",newUser.getUserName(),e.getMessage());
                log.debug("having bug in {} {} : ",newUser.getUserName(),e.getMessage());
            }



        }
    public void saveNewAdmin(User newUser) {
        String hashedPswd = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPswd);
        newUser.setRoles(Arrays.asList("ADMIN"));
        userRepository.save(newUser);
    }
    public void saveUser(User newUser){
        userRepository.save(newUser);
    }


        public List<User> getAllUsers(){
              return userRepository.findAll();
        }

        public  Optional<User> findUserById(ObjectId myId){
              return userRepository.findById(myId);
        }

//        public  void deleteUserById(ObjectId myId){
//            userRepository.deleteById(myId);
//        }

        public  User findByUserName(String username){
             return  userRepository.findByUserName(username);
        }


}
