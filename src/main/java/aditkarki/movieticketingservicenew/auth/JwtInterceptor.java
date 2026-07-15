package aditkarki.movieticketingservicenew.auth;

import aditkarki.movieticketingservicenew.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component

@RequiredArgsConstructor
public class JwtInterceptor extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");// Gives us access to the header containing our token/creds

        if (authHeader == null && !shouldSkipAuth.contains(request.getRequestURI())) {
            return;
        }

         // Change this authHeader condition
            String[] split = authHeader.split(" ", 2); // Avoids Bearer prefix to get obtain token
            String token = split[1];

            if (jwtUtils.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtils.extractEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetails(request)); // Grabs information like IP addy and Session ID
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        chain.doFilter(request, response);
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/api/auth/login") || request.getRequestURI().startsWith("/api/auth/registration");
    }

    private List<String> shouldSkipAuth = List.of(
            "/login"
    );
}
