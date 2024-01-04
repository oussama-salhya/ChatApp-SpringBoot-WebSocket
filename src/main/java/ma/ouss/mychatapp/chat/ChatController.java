package ma.ouss.mychatapp.chat;

import ma.ouss.mychatapp.dao.AppUserRepository;
import ma.ouss.mychatapp.dao.ChatMessageRepository;
import ma.ouss.mychatapp.dao.LogRepository;
import ma.ouss.mychatapp.dto.ChatMessageDto;
import ma.ouss.mychatapp.entities.AppUser;
import ma.ouss.mychatapp.entities.ChatMessage;
import ma.ouss.mychatapp.entities.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ma.ouss.mychatapp.dao.LogRepository LogRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDto sendMessage(
            @Payload ChatMessageDto chatMessageDto
    ) {
        Date date = new Date();
        AppUser user = appUserRepository.findByUsername(chatMessageDto.getSender());
        Log log =Log.builder()
                .type(MessageType.CHAT)
                .sender(user)
                .build();
        LogRepository.save(log);
        ChatMessage chatMessage = ChatMessage.builder()
                .content(chatMessageDto.getContent())
                .date(date)
                .sender(user)
                .log(log)
                .build();
        chatMessageRepository.save(chatMessage);
        user.setLastConnectonDate(null);
        user.getChatMessages().add(chatMessage);
        appUserRepository.save(user);
        chatMessageDto.setDate(new java.util.Date());
        return chatMessageDto;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDto addUser(
            @Payload ChatMessageDto chatMessageDto,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        Date date = new Date();
        AppUser user = appUserRepository.findByUsername(chatMessageDto.getSender());
        Log log =Log.builder()
                .type(MessageType.JOIN)
                .sender(user)
                .build();
        LogRepository.save(log);
        ChatMessage chatMessage = ChatMessage.builder()
                .content("user connected")
                .date(null)
                .sender(user)
                .log(log)
                .build();
        chatMessageRepository.save(chatMessage);
        user.setLastConnectonDate(null);
        user.getChatMessages().add(chatMessage);
        appUserRepository.save(user);
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessageDto.getSender());
        chatMessageDto.setDate(date);
        return chatMessageDto;
    }
}