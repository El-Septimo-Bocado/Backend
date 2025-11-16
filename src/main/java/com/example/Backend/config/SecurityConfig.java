package com.example.Backend.config;

import com.example.Backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 🔹 CORS integrado aquí
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://127.0.0.1:5500",
                            "http://localhost:5500"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    // no estás usando cookies del navegador contra el back
                    config.setAllowCredentials(false);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // 🔹 Permitir preflight OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔹 Swagger y raíz
                        .requestMatchers("/", "/error",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()

                        // 🔹 Auth (login/register)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🔹 TODO showtimes público (GET + POST holds + release)
                        .requestMatchers("/api/showtimes/**").permitAll()

                        // 🔹 Órdenes también públicas por ahora (crear, pagar, recibo)
                        .requestMatchers("/api/orders/**").permitAll()

                        // 🔹 Lecturas de movies/menu (ya cubiertas, pero por claridad)
                        .requestMatchers("/api/movies/**", "/api/menu/**").permitAll()

                        // 🔹 El resto sí requiere login
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}