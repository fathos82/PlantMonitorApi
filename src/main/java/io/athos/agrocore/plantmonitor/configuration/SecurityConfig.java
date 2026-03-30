package io.athos.agrocore.plantmonitor.configuration;

import io.athos.agrocore.plantmonitor.security.JwtAuthenticationFilter;
import io.athos.agrocore.plantmonitor.security.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    @Lazy
    private JwtAuthenticationFilter jwtAuthenticationFilter;

//    @Autowired
//    private SecurityUserDetailsService userDetailsService;




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(
                                        "/api/auth/logout/**",
                                        "/api/auth/login/**",
                                        "/api/auth/register/**",
                                        "/api/auth/refresh/**",
                                        "/api/auth/confirm-email/**",
                                        "/api/auth/resend-email/**",
                                        "/api/auth/forgot-password/**",
                                        "/api/auth/reset-password/**",
                                        "/api/auth/confirm-delete-account/**").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/devices/").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/devices/from_device/").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/sensors/").permitAll()
                                .anyRequest().authenticated()

                )

                // Define a política de sessão como stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Adiciona seu filtro JWT antes do filtro de autenticação padrão
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return userDetailsService;
//    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
