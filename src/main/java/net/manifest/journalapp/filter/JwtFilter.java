package net.manifest.journalapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.manifest.journalapp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 1. Check if the Authorization header is present and correctly formatted.
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try{
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                // Handle exceptions for expired or invalid tokens
                logger.warn("JWT token processing error: " + e.getMessage());
            }
        }

        // 2. If we have a username and there's no existing authentication in the context...
        if(username != null  && SecurityContextHolder.getContext().getAuthentication() == null){
            // ...load the user's details from the database.

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Boolean isValidToken = jwtUtils.validateToken(token);
            if(Boolean.TRUE.equals(isValidToken)){
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
       // response.addHeader("admin","Manish");
        // 4. Continue the filter chain for the next filter to process.
        filterChain.doFilter(request,response);
    }
}
