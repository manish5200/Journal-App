package net.manifest.journalapp.config;

import net.manifest.journalapp.config.security.ApiOAuth2AuthenticationSuccessHandler;
import net.manifest.journalapp.filter.JwtFilter;
import net.manifest.journalapp.services.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("dev") // This configuration is only active when the "dev" profile is used.
@EnableMethodSecurity // This is needed for @PreAuthorize to work
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    private ApiOAuth2AuthenticationSuccessHandler apiOAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // We build a single, continuous chain of configuration on the 'http' object.
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        // 1. Define all public URLs first.
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/auth/google/**",
                                "/login/oauth2/code/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()
                        // 2. Define rules for specific roles.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 3. Define a general rule for all other authenticated requests.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(apiOAuth2AuthenticationSuccessHandler)
                )
                // 4. Add our custom JWT filter to the chain.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 5. Finally, afterAA all configuration is added, call .build() and return the result.
        // This creates the SecurityFilterChain that the method requires.
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}

