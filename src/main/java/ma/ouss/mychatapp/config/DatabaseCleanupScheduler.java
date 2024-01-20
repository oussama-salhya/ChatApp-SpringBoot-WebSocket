package ma.ouss.mychatapp.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.ouss.mychatapp.dao.LogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanupScheduler {

    private final LogRepository logRepository; // Replace YourRepository with the actual repository for your entity

    public DatabaseCleanupScheduler(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

//    @Scheduled(cron = "0 0 * * * *")// This cron expression triggers the method every hour
    @Scheduled(cron = "0 0/5 * * * *") // This cron expression triggers the method every 5 minutes
    public void cleanupDatabase() {
        // Delete all rows from the table
        logRepository.deleteAll();
    }
}
