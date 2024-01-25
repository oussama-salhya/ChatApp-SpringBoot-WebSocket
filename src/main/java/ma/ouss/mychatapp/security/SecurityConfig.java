package ma.ouss.mychatapp.security;

import lombok.AllArgsConstructor;
import ma.ouss.mychatapp.service.AccountService;
import ma.ouss.mychatapp.service.UserDetailsServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.util.Assert;

import javax.sql.DataSource;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{
    private PasswordEncoder passwordEncoder;
    private UserDetailsServiceImpl userDetailsServiceImpl;
//       @Bean
    CommandLineRunner commandLineRunnerUserDetails(AccountService accountService) {
        return args -> {
            accountService.addNewRole("USER");
            accountService.addNewRole("MODERATOR");
            accountService.addNewRole("ADMIN");

            accountService.addNewUser("user1", "demo1234", "user1@", "demo1234");
            accountService.addNewUser("user2", "demo1234", "user2@","demo1234");
            accountService.addNewUser("user3", "demo1234", "user2@","demo1234");
            accountService.addNewUser("user4", "demo1234", "user2@","demo1234");
            accountService.addNewUser("admin1", "demo1234", "admin@", "demo1234");
            accountService.addNewUser("admin2", "demo1234", "demo@", "demo1234");
            accountService.addNewUser("admin3", "demo1234", "demo@", "demo1234");

            accountService.addRoleToUser("user1", "USER");
            accountService.addRoleToUser("user2", "USER");
            accountService.addRoleToUser("user3", "USER");
            accountService.addRoleToUser("user4", "USER");
            accountService.addRoleToUser("user2", "MODERATOR");

            accountService.addRoleToUser("admin1", "USER");
            accountService.addRoleToUser("admin1", "MODERATOR");
            accountService.addRoleToUser("admin1", "ADMIN");

            accountService.addRoleToUser("admin2", "USER");
            accountService.addRoleToUser("admin2", "MODERATOR");
            accountService.addRoleToUser("admin2", "ADMIN");

            accountService.addRoleToUser("admin3", "USER");
            accountService.addRoleToUser("admin3", "MODERATOR");
            accountService.addRoleToUser("admin3", "ADMIN");


        };
    }

    //    configuration -------------------------------------------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/index");
//        http.formLogin().defaultSuccessUrl("/").permitAll();
//      on desactive csrf pour pouvoir utiliser postman ou pour utiliser authentification basé sur token
//        http.csrf().disable();
//        http.csrf((csrf) -> csrf
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//        );
//        http.csrf().csrfTokenRepository(csrfTokenRepository());
//        http.rememberMe();
//        http.authorizeHttpRequests().requestMatchers("/Client/**").hasRole("USER");
//        http.authorizeHttpRequests().requestMatchers("/admin/**","/css/**").hasRole("ADMIN");
        http.authorizeRequests().requestMatchers("/css/**").permitAll();
        http.authorizeRequests().requestMatchers("/img/**").permitAll();
        http.authorizeRequests().requestMatchers("/static/**").permitAll();
        http.authorizeRequests().requestMatchers("/register").permitAll();
        http.authorizeRequests().requestMatchers("/login").permitAll();
        http.authorizeRequests().requestMatchers("/echec").permitAll();
//        http.authorizeRequests().requestMatchers("/api/**").permitAll();
        http.authorizeRequests().requestMatchers("/authentication").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.exceptionHandling().accessDeniedPage("/echec");
//        http.authorizeRequests().requestMatchers("/api/csrf-token").permitAll() // Allow access to CSRF token endpoint
//                .anyRequest().authenticated()
//                .and()
//                .csrf()
//                .csrfTokenRepository(csrfTokenRepository());
//        use this if u use userDetailsService() authentication
        http.userDetailsService(userDetailsServiceImpl);
        http.csrf().disable();
//        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }
}
