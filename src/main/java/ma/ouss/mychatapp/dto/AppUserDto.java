package ma.ouss.mychatapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.ouss.mychatapp.entities.AppRole;

import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AppUserDto {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Date lastConnectonDate ;
    private String status;
    private Boolean banned = false;
    private List<AppRole> appRoles ;
    public AppUserDto(String username, String password, String email, String firstName, String lastName, Date lastConnectonDate,Boolean banned) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastConnectonDate = lastConnectonDate;
        if (lastConnectonDate == null) {
            this.status = "online";
        } else {
            this.status = "offline";
        }
        this.banned = banned;
    }

}
