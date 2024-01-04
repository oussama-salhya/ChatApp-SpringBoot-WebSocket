package ma.ouss.mychatapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Date lastConnectonDate ;
    Boolean Baned = false;
    public AppUser(String username) {
        this.username = username;
    }

    @OneToMany(mappedBy = "sender",fetch = FetchType.EAGER)
    private List<ChatMessage> chatMessages ;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<AppRole> appRoles ;
}