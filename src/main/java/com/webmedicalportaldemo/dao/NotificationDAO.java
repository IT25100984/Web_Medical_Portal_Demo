package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.dto.NotificationDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NotificationDAO {

    private final JdbcTemplate jdbcTemplate;

    public NotificationDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** Inserts a reminder unless an identical one exists. Returns true only if a new row was created. */
    public boolean createIfAbsent(int userID, int appointmentID, String reminderType,
                                  LocalDateTime apptStart, String message) {
        String sql = "INSERT IGNORE INTO notifications " +
                "(user_id, appointment_id, reminder_type, appt_start, message) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, userID, appointmentID, reminderType,
                Timestamp.valueOf(apptStart), message) > 0;
    }

    /**
     * Unread reminders that are still accurate: the appointment must still be CONFIRMED,
     * still at the time the reminder was written for, and not yet started.
     */
    public List<NotificationDTO> findUnread(int userID) {
        String sql = "SELECT n.notification_id, n.appointment_id, n.reminder_type, n.message " +
                "FROM notifications n JOIN appointments a ON a.appointment_id = n.appointment_id " +
                "WHERE n.user_id = ? AND n.is_read = FALSE " +
                "AND a.status = 'CONFIRMED' " +
                "AND TIMESTAMP(a.appt_date, a.appt_time) = n.appt_start " +
                "AND n.appt_start > NOW() " +
                "ORDER BY n.appt_start ASC, n.notification_id ASC LIMIT 20";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new NotificationDTO(
                rs.getInt("notification_id"),
                rs.getInt("appointment_id"),
                rs.getString("reminder_type"),
                rs.getString("message")), userID);
    }

    /** Ownership is enforced in SQL: a user can only mark their own notifications. */
    public boolean markRead(int notificationID, int userID) {
        return jdbcTemplate.update(
                "UPDATE notifications SET is_read = TRUE WHERE notification_id = ? AND user_id = ?",
                notificationID, userID) > 0;
    }

    public int markAllRead(int userID) {
        return jdbcTemplate.update(
                "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE", userID);
    }
}