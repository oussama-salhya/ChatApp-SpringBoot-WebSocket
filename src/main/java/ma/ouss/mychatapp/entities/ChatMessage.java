
package ma.ouss.mychatapp.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.ouss.mychatapp.entities.AppUser;
import ma.ouss.mychatapp.entities.Log;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;


    private Date date;

    @ManyToOne
    private AppUser sender;

    @OneToOne
    private Log log;

}
