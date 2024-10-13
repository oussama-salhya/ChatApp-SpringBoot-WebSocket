
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
@Table(name = "comfychat_chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;


    private Date date;

    @ManyToOne
    private AppUser sender;

    @ManyToOne
    @JoinColumn(name = "log_id")
    private Log log;

}
