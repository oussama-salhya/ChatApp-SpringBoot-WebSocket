package ma.ouss.mychatapp.service;

import lombok.AllArgsConstructor;
import ma.ouss.mychatapp.dao.AppRoleRepository;
import ma.ouss.mychatapp.dao.AppUserRepository;
import ma.ouss.mychatapp.entities.AppUser;
import ma.ouss.mychatapp.entities.AppRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;


@Service
@Transactional
@AllArgsConstructor // Autowired is deprecated so we use this annotation inject dependencies
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    @Override
    public AppUser addNewUser(String username, String password, String email, String confirmedPassword) {
        if (!password.equals(confirmedPassword)) throw new RuntimeException("Please confirm your password");
        AppUser user = appUserRepository.findByUsername(username);
        if (user != null) throw new RuntimeException("User already exists");
        AppUser appUser = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .lastConnectonDate(new Date())
                .banned(false)
                .build();
        appUserRepository.save(appUser);
        return appUser;
    }

    @Override
    public AppRole addNewRole(String role) {
        if (appRoleRepository.findById(role).isPresent()) throw new RuntimeException("Role already exists");
        AppRole appRole = AppRole.builder()
                .role(role)
                .build();
        appRoleRepository.save(appRole);
        return appRole;
    }

    @Override
    public void addRoleToUser(String username, String role) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findById(role).orElseThrow(() -> new RuntimeException("Role not found"));
        appUser.getAppRoles().add(appRole);
        appUserRepository.save(appUser);
    }

    @Override
    public void removeRoleFromUser(String username, String role) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findById(role).orElseThrow(() -> new RuntimeException("Role not found"));
        appUser.getAppRoles().remove(appRole);
        appUserRepository.save(appUser);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);

    }

    @Override
    public void saveUser(AppUser user) {
        appUserRepository.save(user);
    }
//    on peut ajouter d'autres méthodes par exemple pour verifier ou valider email, activer compte, etc.
}
