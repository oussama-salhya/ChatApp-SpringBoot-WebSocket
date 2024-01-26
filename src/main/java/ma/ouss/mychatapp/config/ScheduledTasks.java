package ma.ouss.mychatapp.config;

////    @Scheduled(cron = "0 0 * * * *")// This cron expression triggers the method every hour
//    @Scheduled(cron = "0 0/2 * * * *") // This cron expression triggers the method every 5 minutes

import ma.ouss.mychatapp.dao.ChatMessageRepository;
import ma.ouss.mychatapp.dao.LogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private final LogRepository logRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ScheduledTasks(LogRepository logRepository, ChatMessageRepository chatMessageRepository) {
        this.logRepository = logRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Scheduled(cron = "0 0/20 * * * *")
    public void cleanupDatabase() {
        System.out.println("cleaniiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiing");
        chatMessageRepository.deleteAll();
        logRepository.deleteAll();
    }
}