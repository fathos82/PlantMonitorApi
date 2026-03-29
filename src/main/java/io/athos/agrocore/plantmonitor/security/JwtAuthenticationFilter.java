package io.athos.agrocore.plantmonitor.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SecurityUserDetailsService securityUserDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Endpoints públicos
        if (path.startsWith("/api/auth/") && !path.equals("/api/auth/me/")) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String token = extractTokenFromHeader(request);
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtService.extractUsername(token);
                SecurityUser securityUser = securityUserDetailService.loadUserByUsername(username);
                if (jwtService.validateToken(token, securityUser)) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            securityUser, null, securityUser.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else if (request.getHeader("Authorization") == null || token == null) {
                throw new JwtException("Token vazio");
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UsernameNotFoundException ex) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");

        } catch (JwtException ex) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }

    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"detail\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}
