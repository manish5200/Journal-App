package net.manifest.journalapp.config;

import net.manifest.journalapp.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class SecurityConfigProd {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return  http.authorizeHttpRequests(request -> request
                        .anyRequest().authenticated()  // all except journal requests does not need authentication
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    /*
    This configureGlobal method is part of the old,
     deprecated way of configuring Spring Security.
     It creates the exact circular dependency we
     discussed because it tries to manually wire up the
     UserDetailsService and PasswordEncoder while the
     security configuration itself is still being built.
     */

   /* @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
          auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    } */

    @Bean
    public  PasswordEncoder passwordEncoder(){
          return  new BCryptPasswordEncoder();
    }
}
