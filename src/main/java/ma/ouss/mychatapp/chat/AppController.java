package ma.ouss.mychatapp.chat;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import ma.ouss.mychatapp.dao.AppUserRepository;
import ma.ouss.mychatapp.dao.ChatMessageRepository;
import ma.ouss.mychatapp.dto.AppUserDto;
import ma.ouss.mychatapp.dto.ChatMessageDto;
import ma.ouss.mychatapp.entities.AppUser;
import ma.ouss.mychatapp.entities.ChatMessage;
import ma.ouss.mychatapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;


@RestController
@AllArgsConstructor
//@CrossOrigin(origins = "http://localhost:8088")
public class AppController {
    private ChatMessageRepository chatMessageRepository;
    private AppUserRepository appUserRepository;
    private AccountService accountService;
    private SimpMessagingTemplate messagingTemplate;
//    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PutMapping("/api/ban") // Using PUT for banning as it's more appropriate
    public ResponseEntity<String> ban(@RequestBody AppUser userToBan) {
            if (userToBan == null) {
                return ResponseEntity.badRequest().body("User is required");
            }
            String username = userToBan.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }
            AppUser user = accountService.loadUserByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body("Username not exists");
            }

            user.setBanned(true);
            try {
            accountService.removeRoleFromUser(user.getUsername(), "MODERATOR");
            } catch (RuntimeException e) {
                System.out.println("User is not Moderator");
            }
            accountService.saveUser(user);

        return ResponseEntity.ok("User banned successfully");
    }
//@PreAuthorize("hasRole('ROLE_MODERATOR')")
@PutMapping("/api/unban") // Using PUT for banning as it's more appropriate
    public ResponseEntity<String> unban(@RequestBody AppUser userToBan) {
            if (userToBan == null) {
                return ResponseEntity.badRequest().body("User is required");
            }
            String username = userToBan.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }
            AppUser user = accountService.loadUserByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body("Username not exists");
            }

            user.setBanned(false);
            accountService.saveUser(user);

            return ResponseEntity.ok("User banned successfully");
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/api/addModerator") // Using PUT for banning as it's more appropriate
    public ResponseEntity<String> addModerator(@RequestBody AppUser userToBeMod) {
            if (userToBeMod == null) {
                return ResponseEntity.badRequest().body("User is required");
            }
            String username = userToBeMod.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }
            AppUser user = accountService.loadUserByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body("Username not exists");
            }
            accountService.addRoleToUser(user.getUsername(), "MODERATOR");
            accountService.saveUser(user);
//            // Get the current authentication
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            // Modify the roles as needed
//            // For example, remove the "MODERATOR" role
//            authentication.getAuthorities().removeIf(authority -> authority.getAuthority().equals("ROLE_MODERATOR"));
//
//            // Update the authentication in the security context
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok("Moderator added successfully");
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/api/deleteModerator") // Using PUT for banning as it's more appropriate
    public ResponseEntity<String> deleteModerator(@RequestBody AppUser userToBeMod) {
            if (userToBeMod == null) {
                return ResponseEntity.badRequest().body("User is required");
            }
            String username = userToBeMod.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }
            AppUser user = accountService.loadUserByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body("Username not exists");
            }
            accountService.removeRoleFromUser(user.getUsername(), "MODERATOR");
            accountService.saveUser(user);
            return ResponseEntity.ok("Moderator deleted successfully");
    }
    @PreAuthorize("hasRole('ROLE_USER')")
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
        if (oldMessagesDto.isEmpty()) return ResponseEntity.ok(null);
        return ResponseEntity.ok(oldMessagesDto);
    }
    @PreAuthorize("hasRole('ROLE_USER')")
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
                    .banned(user.getBanned())
                    .status(user.getLastConnectonDate() == null ? "online" : "offline")
                    .appRoles(user.getAppRoles())
                    .build());
        }
        return ResponseEntity.ok(usersDto);
    }



}


