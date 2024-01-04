package ma.ouss.mychatapp.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ouss.mychatapp.dao.AppRoleRepository;
import ma.ouss.mychatapp.dao.AppUserRepository;
import ma.ouss.mychatapp.dao.ChatMessageRepository;
import ma.ouss.mychatapp.dao.LogRepository;
import ma.ouss.mychatapp.dto.ChatMessageDto;
import ma.ouss.mychatapp.entities.AppUser;
import ma.ouss.mychatapp.entities.ChatMessage;
import ma.ouss.mychatapp.chat.MessageType;
import ma.ouss.mychatapp.entities.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Date;


@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private AppUserRepository  appUserRepository;
    @Autowired
    private LogRepository LogRepository;

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {

            AppUser user = appUserRepository.findByUsername(username);
             Date date = new Date();
             Log log =Log.builder()
                     .type(MessageType.LEAVE)
                     .sender(user)
                     .build();
            LogRepository.save(log);

             ChatMessage chatMessage = ChatMessage.builder()
                     .content("user disconnected")
                     .date(date)
                     .sender(user)
                     .log(log)
                     .build();
            chatMessageRepository.save(chatMessage);
            user.setLastConnectonDate(date);
            user.getChatMessages().add(chatMessage);
            appUserRepository.save(user);
            var chatMessageDto = ChatMessageDto.builder()
                    .type(String.valueOf(MessageType.LEAVE))
                    .sender(username)
                    .date(date)
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessageDto);
        }
    }

}