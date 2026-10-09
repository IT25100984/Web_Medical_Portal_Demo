package com.webmedicalportaldemo.service;

import com.webmedicalportaldemo.dao.AppointmentDAOInterface;
import com.webmedicalportaldemo.dao.NotificationDAO;
import com.webmedicalportaldemo.dto.AppointmentReminderRow;
import com.webmedicalportaldemo.dto.ReminderSummaryDTO;
import com.webmedicalportaldemo.service.ReminderMessageBuilder.ReminderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentReminderService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentReminderService.class);

    private final AppointmentDAOInterface appointmentDAO;
    private final NotificationDAO notificationDAO;
    private final ReminderMessageBuilder messageBuilder;

    public AppointmentReminderService(AppointmentDAOInterface appointmentDAO,
                                      NotificationDAO notificationDAO,
                                      ReminderMessageBuilder messageBuilder) {
        this.appointmentDAO = appointmentDAO;
        this.notificationDAO = notificationDAO;
        this.messageBuilder = messageBuilder;
    }

    /**
     * Runs every 5 minutes. The two windows don't overlap, so an appointment gets a
     * "24H" reminder first and a "1H" reminder later. Re-runs are harmless: the
     * unique key on notifications stops duplicates.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void generateReminders() {
        LocalDateTime now = LocalDateTime.now();
        int created = createRemindersFor(ReminderType.HOUR_BEFORE, now, now.plusHours(1))
                + createRemindersFor(ReminderType.DAY_BEFORE, now.plusHours(1), now.plusHours(24));
        if (created > 0) {
            log.info("Created {} appointment reminder notification(s)", created);
        }
    }

    private int createRemindersFor(ReminderType type, LocalDateTime from, LocalDateTime to) {
        int created = 0;
        for (AppointmentReminderRow row : appointmentDAO.findConfirmedAppointmentsBetween(from, to)) {
            try {
                if (notificationDAO.createIfAbsent(row.patientUserID(), row.appointmentID(), type.code(),
                        row.startsAt(), messageBuilder.build(row, false, type))) {
                    created++;
                }
                if (notificationDAO.createIfAbsent(row.doctorUserID(), row.appointmentID(), type.code(),
                        row.startsAt(), messageBuilder.build(row, true, type))) {
                    created++;
                }
            } catch (Exception exception) {
                // One bad row must not stop the rest of the batch.
                log.error("Could not create reminder for appointment {}: {}", row.appointmentID(), exception.getMessage());
            }
        }
        return created;
    }

    /** Everything the dashboard banner needs, in one call. */
    public ReminderSummaryDTO buildSummary(int userID, boolean isDoctor) {
        LocalDateTime now = LocalDateTime.now();
        List<AppointmentReminderRow> upcoming = appointmentDAO.findUpcomingConfirmedForUser(userID, isDoctor, now);

        ReminderSummaryDTO.NextAppointment next = null;
        if (!upcoming.isEmpty()) {
            AppointmentReminderRow first = upcoming.get(0);
            next = new ReminderSummaryDTO.NextAppointment(
                    first.startsAt().toLocalDate().toString(),
                    first.startsAt().toLocalTime().toString().substring(0, 5),
                    messageBuilder.formatType(first.appointmentType()),
                    messageBuilder.counterpartName(first, isDoctor),
                    Math.max(0, Duration.between(now, first.startsAt()).toMinutes()));
        }
        int today = (int) upcoming.stream()
                .filter(row -> row.startsAt().toLocalDate().equals(now.toLocalDate()))
                .count();

        return new ReminderSummaryDTO(notificationDAO.findUnread(userID), next, upcoming.size(), today);
    }
}