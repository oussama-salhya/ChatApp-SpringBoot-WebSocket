package ma.ouss.mychatapp.dao;

import ma.ouss.mychatapp.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
    //    Boolean existsAppUserByUsername(String username);
    Boolean existsByUsername(String username);
}
