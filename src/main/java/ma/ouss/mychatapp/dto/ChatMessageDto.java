package ma.ouss.mychatapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.ouss.mychatapp.entities.ChatMessage;

import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ChatMessageDto {
     String sender;
     String type;
     String content;
     Date date;


}
