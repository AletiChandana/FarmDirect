
package farmdirect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/", "/index.html", "/login.html",
                                "/register.html", "/favicon.ico",
                                "/css/**", "/js/**", "/images/**",
                                "/api/auth/register"
                        ).permitAll()

                        // Admin dashboard and admin APIs
                        .requestMatchers(
                                "/admin.html", "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Farmer dashboard and orders
                        .requestMatchers(
                                "/api/farmer/orders/**"
                        ).hasRole("FARMER")

                        // Product viewing
                        .requestMatchers(
                                HttpMethod.GET, "/api/products"
                        ).permitAll()

                        // Product management
                        .requestMatchers(
                                HttpMethod.POST, "/api/products"
                        ).hasRole("FARMER")

                        .requestMatchers(
                                "/api/products/**"
                        ).hasRole("FARMER")

                        // Customer checkout
                        .requestMatchers(
                                HttpMethod.POST, "/api/orders"
                        ).permitAll()

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {

                            boolean admin = authentication.getAuthorities()
                                    .stream()
                                    .anyMatch(authority ->
                                            authority.getAuthority().equals("ROLE_ADMIN"));

                            boolean farmer = authentication.getAuthorities()
                                    .stream()
                                    .anyMatch(authority ->
                                            authority.getAuthority().equals("ROLE_FARMER"));

                            if (admin) {
                                response.sendRedirect("/admin.html");
                            } else if (farmer) {
                                response.sendRedirect("/farmer.html");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .failureUrl("/login.html?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login.html?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
