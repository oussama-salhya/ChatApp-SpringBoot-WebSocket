package ma.ouss.mychatapp.service;

import lombok.AllArgsConstructor;
import ma.ouss.mychatapp.entities.AppUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
//this class is used to load user from database to our application
public class UserDetailsServiceImpl implements UserDetailsService {
    private AccountService AccountService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = AccountService.loadUserByUsername(username);
        if (appUser == null) throw new UsernameNotFoundException(String.format("User %s not found", username));
        String[] roles = appUser.getAppRoles().stream().map(r -> r.getRole()).toArray(String[]::new);
//        List<SimpleGrantedAuthority> authorities
//                = appUser.getAppRoles().stream().map(r -> new SimpleGrantedAuthority(r.getRole()))
//                .collect(Collectors.toList());
        UserDetails userDetails =  User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
//                .authorities(authorities)
                .roles(roles)
                .build();
        return userDetails;
    }
}
