package com.example.demo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${app.cors.allowed-origins:localhost:3000}") String allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/auth/login",
                                "/auth/idoso/login",
                                "/auth/verificar-2fa",
                                "/auth/reenviar-codigo",
                                "/swagger-ui/**",
                                "/auth/recuperar-senha",
                                "/auth/verificar-recuperacao",
                                "/auth/nova-senha",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/administrador/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR") //
                        .requestMatchers("/instituicao/**").hasAnyRole("ADMINISTRADOR", "INSTITUICAO")
                        .requestMatchers("/cuidador/**").hasAnyRole("CUIDADOR", "INSTITUICAO")
                        .requestMatchers("/idoso/**").hasAnyRole("CUIDADOR", "INSTITUICAO")
                        .requestMatchers("/remedio/**").hasRole("CUIDADOR")
                        .requestMatchers("/prescricao/**").hasRole("CUIDADOR")
                        .requestMatchers(HttpMethod.GET, "/alertas/me", "/alerta/me").hasRole("IDOSO")
                        .requestMatchers(HttpMethod.PUT, "/alertas/*/confirmar").hasRole("IDOSO")
                        .requestMatchers("/alerta/**").hasRole("CUIDADOR")
                        .requestMatchers("/alertas/**").hasRole("CUIDADOR")
                        .requestMatchers("/contato/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(parseAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private List<String> parseAllowedOrigins() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .map(origin -> origin.startsWith("http://") || origin.startsWith("https://")
                        ? origin
                        : "http://" + origin)
                .toList();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
