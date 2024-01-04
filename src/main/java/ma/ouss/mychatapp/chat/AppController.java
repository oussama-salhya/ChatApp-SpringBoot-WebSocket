package ma.ouss.mychatapp.chat;

import ma.ouss.mychatapp.dao.AppUserRepository;
import ma.ouss.mychatapp.dao.ChatMessageRepository;
import ma.ouss.mychatapp.dto.AppUserDto;
import ma.ouss.mychatapp.dto.ChatMessageDto;
import ma.ouss.mychatapp.entities.AppUser;
import ma.ouss.mychatapp.entities.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AppController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @GetMapping("/api/messages")
    public ResponseEntity<List<ChatMessageDto>> getOldMessages() {
        List<ChatMessage> oldMessages = chatMessageRepository.findAll();
        if (oldMessages.isEmpty()) return ResponseEntity.ok(null);
        // Adjust the query as needed
        List<ChatMessageDto> oldMessagesDto = new ArrayList<>();
        for (ChatMessage oldMessage : oldMessages) {
            oldMessagesDto.add(ChatMessageDto.builder()
                    .content(oldMessage.getContent())
                    .sender(oldMessage.getSender().getUsername())
                    .date(oldMessage.getDate())
                    .type(oldMessage.getLog().getType().toString())
                    .build());
        }
        return ResponseEntity.ok(oldMessagesDto);
    }
    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<AppUserDto>> getUsers() {
        // Fetch the list of users from your repository or service
        List<AppUser> users = appUserRepository.findAll();
        if (users.isEmpty()) return ResponseEntity.ok(null);
        List<AppUserDto> usersDto = new ArrayList<>();
        for (AppUser user : users) {
            usersDto.add(AppUserDto.builder()
                    .username(user.getUsername())
                    .status(user.getLastConnectonDate() == null ? "online" : "offline")
                    .build());
        }
        return ResponseEntity.ok(usersDto);
    }

}
