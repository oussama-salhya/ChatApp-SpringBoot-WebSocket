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
@Table(name = "comfychat_app_user")
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
    Boolean banned = false;
    public AppUser(String username) {
        this.username = username;
         banned = false;
    }

    @OneToMany(mappedBy = "sender",fetch = FetchType.EAGER)
    private List<ChatMessage> chatMessages ;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<AppRole> appRoles ;
}