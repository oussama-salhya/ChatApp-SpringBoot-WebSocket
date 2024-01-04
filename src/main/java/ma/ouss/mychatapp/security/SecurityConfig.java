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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private PasswordEncoder passwordEncoder;
    private UserDetailsServiceImpl userDetailsServiceImpl;
       @Bean
    CommandLineRunner commandLineRunnerUserDetails(AccountService accountService) {
        return args -> {
            accountService.addNewRole("USER");
            accountService.addNewRole("MODERATOR");
            accountService.addNewRole("ADMIN");

            accountService.addNewUser("user1", "1234", "user1@", "1234");
            accountService.addNewUser("user2", "1234", "user2@","1234");
            accountService.addNewUser("admin", "1234", "admin@", "1234");

            accountService.addRoleToUser("user1", "USER");
            accountService.addRoleToUser("user2", "USER");
            accountService.addRoleToUser("user2", "MODERATOR");
            accountService.addRoleToUser("admin", "USER");
            accountService.addRoleToUser("admin", "MODERATOR");
            accountService.addRoleToUser("admin", "ADMIN");

        };
    }

    //    configuration -------------------------------------------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/index");
        http.formLogin().defaultSuccessUrl("/").permitAll();
//      on desactive csrf pour pouvoir utiliser postman ou pour utiliser authentification basé sur token
//        http.csrf().disable();
//        http.rememberMe();
//        http.authorizeHttpRequests().requestMatchers("/Client/**").hasRole("USER");
//        http.authorizeHttpRequests().requestMatchers("/admin/**","/css/**").hasRole("ADMIN");
        http.authorizeRequests().requestMatchers("/css/**").permitAll();
        http.authorizeRequests().requestMatchers("/register").permitAll();
        http.authorizeRequests().requestMatchers("/api/**").permitAll();
        http.authorizeRequests().requestMatchers("/authentication").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.exceptionHandling().accessDeniedPage("/echec");
//        use this if u use userDetailsService() authentication
        http.userDetailsService(userDetailsServiceImpl);
        return http.build();
    }

}
