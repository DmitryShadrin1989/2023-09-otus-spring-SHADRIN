package ru.otus.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/library/book/*/comment/new").hasAnyRole("ADMIN", "READER")
                        .requestMatchers("/library/book/*/comment").hasAnyRole("ADMIN", "READER")
                        .requestMatchers("/api/library/book/*/comment").hasAnyRole("ADMIN", "READER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/library/book/*/comment/*").hasAnyRole("ADMIN", "READER")
                        .requestMatchers(HttpMethod.GET,"/library/book/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/library/book/*/comment/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/library/book").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .anonymous(Customizer.withDefaults())
                .rememberMe(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}