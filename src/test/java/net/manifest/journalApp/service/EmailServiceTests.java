package net.manifest.journalApp.service;


import net.manifest.journalApp.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

       @Autowired
       private EmailService emailService;

       @Test
       public void testSendMail(){

           //Simple Email Message
           /*emailService.sendEmail("manishneelambar@gmail.com",
                   "Testing java mail sender service",
                   "How are you my EmailService."); */

           //Email with the help of mimeEmail -> Added sender name and email too
            emailService.sendEmail("manishneelambar@gmail.com",
                    "Testing java mail sender service",
                    "How are you my EmailService.",
                    "Journal-App");
       }
}
