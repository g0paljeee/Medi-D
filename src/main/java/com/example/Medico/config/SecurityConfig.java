package com.example.Medico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        UserDetails receptionist = User.withUsername("receptionist")
                .password(encoder.encode("recep123"))
                .roles("RECEPTIONIST")
                .build();

        UserDetails doctor = User.withUsername("doctor")
                .password(encoder.encode("doctor123"))
                .roles("DOCTOR")
                .build();

        UserDetails pharmacist = User.withUsername("pharmacist")
                .password(encoder.encode("pharma123"))
                .roles("PHARMACIST")
                .build();

        return new InMemoryUserDetailsManager(receptionist, doctor, pharmacist);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/doctor/medicines", "/error").permitAll()
                        .requestMatchers("/api/pharmacy/**").hasRole("PHARMACIST")
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/api/patients/**", "/api/appointments/**").hasAnyRole("RECEPTIONIST","DOCTOR")
                        .anyRequest().authenticated()
                )
                .httpBasic();

        return http.build();
    }
}
