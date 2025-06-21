// src/main/java/br/unialfa/hackathon/security/SecurityConfig.java
package br.unialfa.hackathon.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Adicione a injeção do seu UserDetailsService customizado
    // Se ainda não tiver um, vamos criar em seguida
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter; // Injete seu filtro JWT

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para configurar o AuthenticationProvider que usará seu UserDetailsService
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para APIs REST
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso a endpoints públicos (login, cadastro, recursos estáticos)
                        .requestMatchers("/auth/login", "/cadastro", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // Protege endpoints da API REST
                        .requestMatchers("/turmas/**", "/provas/**", "/respostas/**", "/usuarios/**").authenticated() // Exige autenticação para essas rotas
                        // Permite acesso a /home após login (se for um endpoint de Thymeleaf)
                        .requestMatchers("/home").authenticated()
                        // Qualquer outra requisição exige autenticação (pode ajustar conforme sua necessidade)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")        // URL da sua página de login customizada
                        .defaultSuccessUrl("/home", true) // Redireciona para /home após login bem-sucedido
                        .failureUrl("/login?error") // Redireciona para /login com erro em caso de falha
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")       // URL para deslogar
                        .logoutSuccessUrl("/login?logout") // Redireciona para /login com mensagem de logout
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider()) // Define o AuthenticationProvider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro JWT

        return http.build();
    }
}