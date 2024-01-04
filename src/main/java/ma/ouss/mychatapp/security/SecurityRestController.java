package ma.ouss.mychatapp.security;

import lombok.AllArgsConstructor;
import ma.ouss.mychatapp.dao.AppRoleRepository;
import ma.ouss.mychatapp.dao.AppUserRepository;
import ma.ouss.mychatapp.entities.AppUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class SecurityRestController {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    @GetMapping("/authentication")
    public Authentication authentication(Authentication authentication) {
//        other way to get authentication
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     return authentication;
    }
//    you can test it in postman but u must disable csrf in security config
//    and u must add the username and password in the body of the request in form-data !
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password){
        if (appUserRepository.existsByUsername(username)) return ResponseEntity.badRequest().body("User already exists");
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.getAppRoles().add(appRoleRepository.findById("USER").get());
        appUserRepository.save(appUser);
        return ResponseEntity.ok("User created");
    }
//    for this in postman u must add the username and password in the body of the request in raw json !
//    @PostMapping("/register")
//    public void register(@RequestBody AppUser appUser){
//        if (appUserRepository.existsByUsername(appUser.getUsername())) throw new RuntimeException("User already exists");
//        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
//        appUser.getAppRoles().add(appRoleRepository.findById("USER").get());
//        appUserRepository.save(appUser);
//    }

}
