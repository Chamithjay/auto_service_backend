//package com.EAD.autoservice_backend.config;
//
//import com.EAD.autoservice_backend.service.CustomUserDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import java.util.Arrays;
//import java.util.List;
//import org.springframework.http.HttpMethod;
//
///**
// * Security Configuration for JWT Authentication
// * Configures Spring Security for stateless JWT authentication with React frontend
// */
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final CustomUserDetailsService userDetailsService;
//
//    @Autowired
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
//                          CustomUserDetailsService userDetailsService) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.userDetailsService = userDetailsService;
//    }
//
//    /**
//     * Password encoder bean for secure password hashing
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }
//
//    /**
//     * Authentication manager bean for handling login
//     */
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    /**
//     * DAO Authentication Provider for database authentication
//     */
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    /**
//     * CORS configuration for React frontend
//     */
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    /**
//     * Main security filter chain configuration
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                // Disable CSRF for Postman / React
//                .csrf(AbstractHttpConfigurer::disable)
//
//                // Authorize requests
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers(
//                                "/api/auth/register",
//                                "/api/auth/login",
//                                "/api/auth/password/**" // for forgot/reset password
//                        ).permitAll()
//
//                        // Swagger endpoints (for testing)
//                        .requestMatchers(
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/swagger-resources/**",
//                                "/webjars/**"
//                        ).permitAll()
//
//                        // Notification endpoints - ROLE-BASED ACCESS
//                        // All authenticated users can READ and UPDATE notifications
//                        .requestMatchers(HttpMethod.GET, "/api/notifications/**").authenticated()
//                        .requestMatchers(HttpMethod.PUT, "/api/notifications/**").authenticated()
//
//                        // Only ADMIN can CREATE and DELETE notifications
//                        .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/notifications/**").hasRole("ADMIN")
//
//                        // Admin endpoints
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        // Profile endpoints
//                        .requestMatchers("/api/profile/**").authenticated()
//
//                        .anyRequest().authenticated()
//                )
//
//                .formLogin(AbstractHttpConfigurer::disable)
//
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//
//                // ADD: JWT filter chain integration
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//
//}



package com.EAD.autoservice_backend.config;

import com.EAD.autoservice_backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpMethod;

/**
 * Security Configuration for JWT Authentication
 * Configures Spring Security for stateless JWT authentication with React frontend
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Password encoder bean for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication manager bean for handling login
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * DAO Authentication Provider for database authentication
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * CORS configuration for React frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Main security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF for Postman / React
                .csrf(AbstractHttpConfigurer::disable)

                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        // Swagger endpoints - MUST BE FIRST
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api-docs/**"
                        ).permitAll()

                        .requestMatchers("/api/chatbot/**").authenticated()

                        // Public endpoints
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/password/**"
                        ).permitAll()

                        // Notification endpoints - ROLE-BASED ACCESS
                        .requestMatchers(HttpMethod.GET, "/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/notifications/**").hasRole("ADMIN")

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Profile endpoints
                        .requestMatchers("/api/profile/**").authenticated()

                        .anyRequest().authenticated()
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // JWT filter - but ignore for Swagger paths
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}