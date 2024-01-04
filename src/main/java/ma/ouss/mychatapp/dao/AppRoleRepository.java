package ma.ouss.mychatapp.dao;
import ma.ouss.mychatapp.entities.AppRole;
import ma.ouss.mychatapp.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, String> {
}
