package net.manifest.journalApp.services;


import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String to , String subject , String body){
        try{
            log.info("Sending email to {}", to);  // {} -> a placeholder for to
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
            log.info("Email sent successfully to {}", to);
        }catch (MailException e){
            log.error("Exception while sending an email to {}",to,e);
        }
    }

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Async
    public void sendEmail(String to ,String subject,String body,String senderName){
          try{
               //Create  MimeMessage instead of SimpleMailMessage
              MimeMessage email = javaMailSender.createMimeMessage();

              //Use the MimeMessageHelper to build the message
              // The 'true' argument enables multipart messages (for attachments, etc.)
              MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(email,true);

              //Set the sender with both email and personal name
              mimeMessageHelper.setFrom(fromEmail,senderName);


              mimeMessageHelper.setTo(to);
              mimeMessageHelper.setSubject(subject);
              mimeMessageHelper.setText(body);

              javaMailSender.send(email);

              log.info("Email successfully sent to '{}' from '{}' having email id '{}' ",to,senderName,fromEmail);
          }catch (Exception e){
               log.error("Exception in sending mail to {}",to,e);
          }
    }
}
