package net.manifest.journalApp.config;

import net.manifest.journalApp.filter.JwtFilter;
import net.manifest.journalApp.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class SecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http) throws Exception {
       return http.authorizeHttpRequests(request -> request
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/journal/**","/user/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())  // all except journal requests does not need authentication

               .csrf(AbstractHttpConfigurer::disable)
               .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
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

//    @Bean
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
//          auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//    }
// This bean is the modern replacement for the old 'configureGlobal' method

    @Bean
    public  PasswordEncoder passwordEncoder(){
          return  new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
}
