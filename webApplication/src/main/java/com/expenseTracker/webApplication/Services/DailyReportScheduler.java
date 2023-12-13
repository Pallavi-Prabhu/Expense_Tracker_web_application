package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Repositories.HomeRepo;
import com.expenseTracker.webApplication.Repositories.LoginRepo;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DailyReportScheduler {

    @Autowired
    EmailService emailService;
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    HomeRepo homeRepo;
    private static final Logger logger = LoggerFactory.getLogger(DailyReportScheduler.class);

    // (cron = "0 30 15 * * ?") // Runs daily at 3:30 PM
//(cron = "0 0 8 1 * ?") // Runs on the 1st day of every month at 8:00 AM
//(cron = "0 0 8 ? * MON") // Runs every Monday at 8:00 AM
    @Scheduled(cron = "0 00 8 ? * FRI")
    public void sendDailyReport() throws MessagingException, IOException {

        logger.info("Inside DailyReportScheduler->DailyReport Email");

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime oneWeekAgo = today.minusWeeks(1);

        LocalDateTime today8AM = today.withHour(8).withMinute(0).withSecond(0);
        LocalDateTime oneWeekAgo8AM = oneWeekAgo.withHour(8).withMinute(0).withSecond(0);


        List<User> users = loginRepo.findAll();
        Map<Long, String> userIdEmailMap = new HashMap<>();
        Map<Long, String> userIdNameMap = new HashMap<>();

        for (User user : users) {
            userIdEmailMap.put(user.getId(), user.getEmail());
            userIdNameMap.put(user.getId(), user.getFirstName());
        }

        for (Map.Entry<Long, String> entry : userIdEmailMap.entrySet()) {
            Long userId = entry.getKey();
            String userEmail = entry.getValue();
            String userName = userIdNameMap.get(userId);
            Float pending = homeRepo.getPendingMoneyForUserIdWeekly(userId,oneWeekAgo8AM ,today8AM );
            Float lent = homeRepo.getLentMoneyForUserIdWeekly(userId,oneWeekAgo8AM ,today8AM);
            if (pending != null || lent != null) {

                if (pending == null) pending = 0f;
                if (lent == null) lent = 0f;
                String reportContent = "Your Pending expenses up to Today from "+ oneWeekAgo.toLocalDate()+": <p>" +
                        "Total due: Rs " + lent + "</p><p>" +
                        "Total Receivables: Rs " + pending + "</p>";
                emailService.sendNotificationEmail(userEmail, "Weekly Expense Report", userName, reportContent);

            }
        }


    }

}

