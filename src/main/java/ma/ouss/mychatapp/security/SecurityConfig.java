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
       @Bean
    CommandLineRunner commandLineRunnerUserDetails(AccountService accountService) {
        return args -> {
            accountService.addNewRole("USER");
            accountService.addNewRole("MODERATOR");
            accountService.addNewRole("ADMIN");
            accountService.addNewRole("DEMO");

            accountService.addNewUser("user1", "oussama", "user1@", "oussama");
            accountService.addNewUser("user2", "oussama", "user2@","oussama");
            accountService.addNewUser("user3", "oussama", "user2@","oussama");
            accountService.addNewUser("user4", "oussama", "user2@","oussama");
            accountService.addNewUser("admin", "oussama", "admin@", "oussama");
            accountService.addNewUser("demoAdminUser", "demo1234", "demo@", "demo1234");
            accountService.addNewUser("demoUser", "demo1234", "demo@", "demo1234");

            accountService.addRoleToUser("user1", "USER");
            accountService.addRoleToUser("user2", "USER");
            accountService.addRoleToUser("user3", "USER");
            accountService.addRoleToUser("user4", "USER");
            accountService.addRoleToUser("user2", "MODERATOR");
            accountService.addRoleToUser("admin", "USER");
            accountService.addRoleToUser("admin", "MODERATOR");
            accountService.addRoleToUser("admin", "ADMIN");

            accountService.addRoleToUser("demoUser", "USER");
            accountService.addRoleToUser("demoUser", "DEMO");

            accountService.addRoleToUser("demoAdminUser", "USER");
            accountService.addRoleToUser("demoAdminUser", "MODERATOR");
            accountService.addRoleToUser("demoAdminUser", "ADMIN");
            accountService.addRoleToUser("demoAdminUser", "DEMO");

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
