package ma.ouss.mychatapp.dao;
import ma.ouss.mychatapp.entities.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

}
