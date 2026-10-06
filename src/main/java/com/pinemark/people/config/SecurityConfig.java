package com.pinemark.people.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Authorization is decided here and in the service layer, never in the template.
 * Anything not explicitly permitted requires an authenticated session.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/actuator/health").permitAll()
                .requestMatchers("/admin/**").hasRole("HR_ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/directory", true))
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"))
            // CSRF stays on. Session fixation protection stays on. Both are defaults
            // worth being explicit about, because the common "fix" is to disable them.
            .sessionManagement(session -> session
                .sessionFixation(sf -> sf.changeSessionId())
                .maximumSessions(2))
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; frame-ancestors 'none'; object-src 'none'; base-uri 'self'"))
                .referrerPolicy(ref -> ref
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Demo accounts only. Real deployments bind to Entra ID over SAML; no password
     * material is stored in this repository, and none is read from the environment
     * at build time.
     */
    @Bean
    InMemoryUserDetailsManager users(PasswordEncoder encoder) {
        UserDetails employee = User.withUsername("r.okonkwo")
                .password(encoder.encode("change-me-in-dev-only"))
                .roles("EMPLOYEE")
                .build();
        UserDetails admin = User.withUsername("h.lindqvist")
                .password(encoder.encode("change-me-in-dev-only"))
                .roles("EMPLOYEE", "HR_ADMIN")
                .build();
        return new InMemoryUserDetailsManager(employee, admin);
    }
}
