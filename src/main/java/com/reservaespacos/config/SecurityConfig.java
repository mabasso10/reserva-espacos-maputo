package com.reservaespacos.config;

import com.reservaespacos.security.JwtAuthFilter;
import com.reservaespacos.security.UsuarioDetailsService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Token JWT obtido em POST /auth/login. Formato: Bearer <token>"
)
public class SecurityConfig {

    @Autowired private JwtAuthFilter jwtAuthFilter;
    @Autowired private UsuarioDetailsService usuarioDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ---- Frontend estático ----
                .requestMatchers(
                    "/", "/index.html", "/dashboard.html",
                    "/clientes.html", "/proprietarios.html", "/espacos.html",
                    "/reservas.html", "/pagamentos.html", "/publico.html",
                    "/css/**", "/js/**", "/images/**", "/*.html", "/*.css", "/*.js"
                ).permitAll()

                // ---- H2 Console (só desenvolvimento) ----
                .requestMatchers("/h2-console/**").permitAll()

                // ---- Swagger / OpenAPI ----
                .requestMatchers(
                    "/swagger-ui.html", "/swagger-ui/**",
                    "/api-docs/**", "/v3/api-docs/**"
                ).permitAll()

                // ---- Autenticação pública ----
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                // ---- Auto-registo público (CLIENTE / PROPRIETARIO) ----
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                // ---- Autenticação protegida (requer token) ----
                // /auth/admin/register → protegido por @PreAuthorize("hasRole('ADMIN')")
                // /auth/senha          → qualquer utilizador autenticado
                // /auth/me             → qualquer utilizador autenticado
                // /auth/utilizadores   → protegido por @PreAuthorize("hasRole('ADMIN')")
                .requestMatchers("/auth/**").authenticated()

                // ---- Endpoint público de espaços ----
                .requestMatchers(HttpMethod.GET, "/espaco/publico").permitAll()

                // ---- Clientes ----
                .requestMatchers(HttpMethod.POST,   "/cliente").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.GET,    "/cliente").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/cliente/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.PUT,    "/cliente/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.DELETE, "/cliente/**").hasRole("ADMIN")

                // ---- Proprietários ----
                .requestMatchers(HttpMethod.POST,   "/proprietario").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers(HttpMethod.GET,    "/proprietario").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/proprietario/**").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers(HttpMethod.PUT,    "/proprietario/**").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers(HttpMethod.DELETE, "/proprietario/**").hasRole("ADMIN")

                // ---- Espaços ----
                .requestMatchers(HttpMethod.GET,    "/espaco").hasAnyRole("ADMIN", "PROPRIETARIO", "CLIENTE")
                .requestMatchers(HttpMethod.GET,    "/espaco/**").hasAnyRole("ADMIN", "PROPRIETARIO", "CLIENTE")
                .requestMatchers(HttpMethod.POST,   "/espaco").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers(HttpMethod.PUT,    "/espaco/**").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers(HttpMethod.DELETE, "/espaco/**").hasAnyRole("ADMIN", "PROPRIETARIO")

                // ---- Reservas ----
                .requestMatchers(HttpMethod.POST,   "/reserva").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.GET,    "/reserva").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers(HttpMethod.GET,    "/reserva/**").hasAnyRole("ADMIN", "PROPRIETARIO", "CLIENTE")
                .requestMatchers(HttpMethod.PUT,    "/reserva/**").hasAnyRole("ADMIN", "PROPRIETARIO")

                // ---- Pagamentos ----
                .requestMatchers(HttpMethod.POST,   "/pagamento").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.GET,    "/pagamento").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers(HttpMethod.GET,    "/pagamento/**").hasAnyRole("ADMIN", "PROPRIETARIO", "CLIENTE")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
