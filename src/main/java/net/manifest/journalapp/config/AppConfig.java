package net.manifest.journalapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * Provides a singleton instance of the PasswordEncoder.
     * By defining it in this separate configuration class, we break the
     * circular dependency that occurred between SecurityConfig and the
     * CustomOAuth2UserService (which also needs to encode passwords).
     *
     * @return A BCryptPasswordEncoder instance for hashing and verifying passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
           return new BCryptPasswordEncoder();
    }

    /**
     * Provides a singleton instance of RestTemplate.
     * Managing RestTemplate as a Spring bean is a best practice, making it
     * easy to configure, reuse, and test across the application.
     *
     * @return A RestTemplate instance for making external HTTP requests.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
