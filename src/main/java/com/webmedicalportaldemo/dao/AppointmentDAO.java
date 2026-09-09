package com.webmedicalportaldemo.dao;
import com.webmedicalportaldemo.dto.AppointmentDTO;
import com.webmedicalportaldemo.service.ApptFileService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@Repository
public class AppointmentDAO implements AppointmentDAOInterface {
    private final JdbcTemplate jdbcTemplate;
    private final ApptFileService apptFileService;
    public AppointmentDAO(DataSource dataSource, ApptFileService apptFileService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.apptFileService = apptFileService;
    }
    private RowMapper<AppointmentDTO> appointmentRowMapper() {
        return (resultSet, rowNumber) -> {
            String rawTime = resultSet.getString("appt_time");
            if (rawTime != null && rawTime.length() >= 5) {
                rawTime = rawTime.substring(0, 5);
            }
            String appointmentDate = resultSet.getString("appt_date");
            String fullDateTime = appointmentDate + " at " + (rawTime == null ? "" : rawTime);
            AppointmentDTO appointment = new AppointmentDTO(
                    resultSet.getInt("appointment_id"),
                    fullDateTime,
                    resultSet.getString("opposite_name"),
                    resultSet.getString("status"),
                    resultSet.getBoolean("is_rescheduled"),
                    resultSet.getInt("last_modified_by"),
                    resultSet.getString("appointment_type"),
                    resultSet.getString("additional_charge"),
                    resultSet.getInt("doctor_id"),
                    resultSet.getInt("patient_id")
            );
            appointment.setTotalFee(resultSet.getBigDecimal("total_fee"));
            return appointment;
        };
    }
    @Override
    public List<AppointmentDTO> getAppointmentsByPatient(int patientUserID) {
        String sql = "SELECT a.appointment_id, a.appt_date, a.appt_time, a.status, " +
                "a.is_rescheduled, a.last_modified_by, a.appointment_type, " +
                "a.additional_charge, a.total_fee, a.doctor_id, a.patient_id, " +
                "CONCAT(du.first_name, ' ', du.last_name) AS opposite_name " +
                "FROM appointments a JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN employees e ON d.employee_pk = e.employee_pk " +
                "JOIN users du ON e.user_id = du.user_id " +
                "WHERE p.user_id = ? AND a.appt_date >= CURDATE() " +
                "ORDER BY CASE WHEN a.appt_date = CURDATE() THEN 1 ELSE 2 END, " +
                "CASE WHEN a.status = 'CONFIRMED' THEN 1 " +
                "WHEN a.status = 'RESCHEDULED' THEN 2 WHEN a.status = 'PENDING' THEN 3 ELSE 4 END, " +
                "a.appt_date ASC, a.appt_time ASC";
        return jdbcTemplate.query(sql, appointmentRowMapper(), patientUserID);
    }
    @Override
    public List<AppointmentDTO> getAppointmentsByDoctor(int doctorUserID) {
        String sql = "SELECT a.appointment_id, a.appt_date, a.appt_time, a.status, " +
                "a.is_rescheduled, a.last_modified_by, a.appointment_type, " +
                "a.additional_charge, a.total_fee, a.doctor_id, a.patient_id, " +
                "CONCAT(pu.first_name, ' ', pu.last_name) AS opposite_name " +
                "FROM appointments a JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN employees e ON d.employee_pk = e.employee_pk " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users pu ON p.user_id = pu.user_id " +
                "WHERE e.user_id = ? AND a.appt_date >= CURDATE() " +
                "ORDER BY CASE WHEN a.appt_date = CURDATE() THEN 1 ELSE 2 END, " +
                "CASE WHEN a.status = 'CONFIRMED' THEN 1 " +
                "WHEN a.status = 'RESCHEDULED' THEN 2 " +
                "WHEN a.status = 'PENDING' THEN 3 ELSE 4 END, " +
                "a.appt_date ASC, a.appt_time ASC";
        return jdbcTemplate.query(sql, appointmentRowMapper(), doctorUserID);
    }
    @Override
    public List<AppointmentDTO> getAllAppointments() {
        String sql = "SELECT a.appointment_id, a.appt_date, a.appt_time, a.status, " +
                "a.is_rescheduled, a.last_modified_by, a.appointment_type, " +
                "a.additional_charge, a.total_fee, a.doctor_id, a.patient_id, " +
                "CONCAT('Doctor: ', du.first_name, ' ', du.last_name, " +
                "' | Patient: ', pu.first_name, ' ', pu.last_name) AS opposite_name " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN employees e ON d.employee_pk = e.employee_pk " +
                "JOIN users du ON e.user_id = du.user_id " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users pu ON p.user_id = pu.user_id " +
                "ORDER BY a.appt_date DESC, a.appt_time DESC";
        return jdbcTemplate.query(sql, appointmentRowMapper());
    }
    @Override
    public boolean doctorHasAccessToPatient(int doctorID, int patientID) {
        if (doctorID <= 0 || patientID <= 0) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND patient_id = ? AND status IN ('CONFIRMED', 'COMPLETED')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, doctorID, patientID);
        return count != null && count > 0;
    }
    @Override
    public boolean bookAppointment(int doctorUserID, int patientUserID, String date, String time, String type, String additionalCharge, BigDecimal totalFee) {
        Integer doctorID = getDoctorIDByUserId(doctorUserID);
        Integer patientID = getPatientIDByUserId(patientUserID);
        if (doctorID == null || patientID == null || doctorID <= 0 || patientID <= 0 || totalFee == null || totalFee.signum() < 0) {
            return false;
        }
        if (date == null || date.isBlank() || time == null || time.isBlank()) {
            return false;
        }
        String normalizedType = normalizeAppointmentType(type);
        String normalizedCharge = additionalCharge == null || additionalCharge.isBlank() ? "NONE" : additionalCharge.trim().toUpperCase();
        if (!"SURGERY".equals(normalizedType)) {
            normalizedCharge = "NONE";
        }
        String checkSql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appt_date = ? AND appt_time = ? AND status <> 'CANCELLED'";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, doctorID, date, time);
        if (count != null && count > 0) {
            return false;
        }
        String insertSql = "INSERT INTO appointments (doctor_id, patient_id, appt_date, " +
                "appt_time, status, is_rescheduled, last_modified_by, appointment_type, " +
                "additional_charge, total_fee) VALUES (?, ?, ?, ?, 'PENDING', FALSE, ?, ?, ?, ?)";
        return jdbcTemplate.update(insertSql, doctorID, patientID, date, time, patientUserID, normalizedType, normalizedCharge, totalFee) > 0;
    }

    @Override
    public boolean updateAppointmentStatus(int appointmentID, String status, String newDate, String newTime, int userID) {
        if (appointmentID <= 0 || status == null || status.isBlank()) {
            return false;
        }
        String normalizedStatus = normalizeStatus(status);
        int rowsAffected;
        if (newDate != null && !newDate.isBlank() && newTime != null && !newTime.isBlank()) {
            String conflictSql = "SELECT COUNT(*) FROM appointments current_appt JOIN appointments other_appt ON current_appt.doctor_id = other_appt.doctor_id WHERE current_appt.appointment_id = ? AND other_appt.appointment_id <> ? AND other_appt.appt_date = ? AND other_appt.appt_time = ? AND other_appt.status <> 'CANCELLED'";
            Integer conflictCount = jdbcTemplate.queryForObject(conflictSql, Integer.class, appointmentID, appointmentID, newDate, newTime);
            if (conflictCount != null && conflictCount > 0) {
                return false;
            }
            String updateSql = "UPDATE appointments SET status = ?, appt_date = ?, appt_time = ?, is_rescheduled = TRUE, last_modified_by = ? WHERE appointment_id = ?";
            rowsAffected = jdbcTemplate.update(updateSql, normalizedStatus, newDate, newTime, userID, appointmentID);
        } else {
            String updateSql = "UPDATE appointments SET status = ?, last_modified_by = ? WHERE appointment_id = ?";
            rowsAffected = jdbcTemplate.update(updateSql, normalizedStatus, userID, appointmentID);
        }
        if (rowsAffected > 0 && "CONFIRMED".equals(normalizedStatus)) {
            synchronizeConfirmedAppointment(appointmentID);
        }
        return rowsAffected > 0;
    }
    @Override
    public boolean cancelAppointment(int appointmentID) {
        if (appointmentID <= 0) {
            return false;
        }
        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE appointment_id = ?";
        return jdbcTemplate.update(sql, appointmentID) > 0;
    }
    @Override
    public boolean setDoctorAvailability(int doctorUserID, Integer dayOfWeek, String startTime, String endTime) {
        Integer doctorID = getDoctorIDByUserId(doctorUserID);
        if (doctorID == null || doctorID <= 0 || dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7 || !isValidTimeRange(startTime, endTime)) {
            return false;
        }
        jdbcTemplate.update("DELETE FROM doctor_availability WHERE doctor_id = ? AND day_of_week = ?", doctorID, dayOfWeek);
        String sql = "INSERT INTO doctor_availability (doctor_id, available_date, day_of_week, start_time, end_time) VALUES (?, NULL, ?, ?, ?)";
        return jdbcTemplate.update(sql, doctorID, dayOfWeek, startTime, endTime) > 0;
    }
    @Override
    public boolean setDoctorAvailability(int doctorUserID, String availableDate, String startTime, String endTime) {
        Integer doctorID = getDoctorIDByUserId(doctorUserID);
        if (doctorID == null || doctorID <= 0 || availableDate == null || availableDate.isBlank() || !isValidTimeRange(startTime, endTime)) {
            return false;
        }
        try {
            LocalDate.parse(availableDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception exception) {
            return false;
        }
        jdbcTemplate.update("DELETE FROM doctor_availability WHERE doctor_id = ? AND available_date = ?", doctorID, availableDate);
        String sql = "INSERT INTO doctor_availability (doctor_id, available_date, day_of_week, start_time, end_time) VALUES (?, ?, NULL, ?, ?)";
        return jdbcTemplate.update(sql, doctorID, availableDate, startTime, endTime) > 0;
    }
    @Override
    public List<String> getAvailableSlots(int doctorUserID, String date) {
        List<String> availableSlots = new ArrayList<>();
        Integer doctorID = getDoctorIDByUserId(doctorUserID);
        if (doctorID == null || doctorID <= 0 || date == null || date.isBlank()) {
            return availableSlots;
        }
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception exception) {
            return availableSlots;
        }
        String databaseDate = localDate.toString();
        int dayOfWeek = localDate.getDayOfWeek().getValue();
        String availabilitySql = "SELECT start_time, end_time FROM doctor_availability WHERE doctor_id = ? AND (available_date = ? OR (available_date IS NULL AND day_of_week = ?)) ORDER BY CASE WHEN available_date = ? THEN 0 ELSE 1 END LIMIT 1";
        jdbcTemplate.query(availabilitySql, resultSet -> {
            int startHour = resultSet.getTime("start_time").toLocalTime().getHour();
            int endHour = resultSet.getTime("end_time").toLocalTime().getHour();
            String bookedSql = "SELECT appt_time FROM appointments WHERE doctor_id = ? AND appt_date = ? AND status <> 'CANCELLED'";
            List<String> bookedHours = jdbcTemplate.query(bookedSql, (bookedResultSet, rowNumber) -> {
                Time bookedTime = bookedResultSet.getTime("appt_time");
                return String.format("%02d:00", bookedTime.toLocalTime().getHour());
            }, doctorID, databaseDate);
            for (int hour = startHour; hour < endHour; hour++) {
                String hourValue = String.format("%02d:00", hour);
                if (!bookedHours.contains(hourValue)) {
                    availableSlots.add(hourValue);
                }
            }
        }, doctorID, databaseDate, dayOfWeek, databaseDate);
        return availableSlots;
    }
    private Integer getDoctorIDByUserId(int userID) {
        if (userID <= 0) {
            return null;
        }
        String sql = "SELECT d.doctor_id FROM doctors d JOIN employees e ON d.employee_pk = e.employee_pk WHERE e.user_id = ?";
        List<Integer> doctorIDs = jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getInt("doctor_id"), userID);
        return doctorIDs.isEmpty() ? null : doctorIDs.get(0);
    }
    private Integer getPatientIDByUserId(int userID) {
        if (userID <= 0) {
            return null;
        }
        String sql = "SELECT patient_id FROM patients WHERE user_id = ?";
        List<Integer> patientIDs = jdbcTemplate.query(sql, (resultSet, rowNumber) -> resultSet.getInt("patient_id"), userID);
        return patientIDs.isEmpty() ? null : patientIDs.get(0);
    }

    private void synchronizeConfirmedAppointment(int appointmentID) {
        try {
            String fetchSql = "SELECT patient_id, doctor_id, appointment_type, " +
                    "total_fee, appt_date, appt_time FROM appointments " +
                    "WHERE appointment_id = ?";
            Map<String, Object> data = jdbcTemplate.queryForMap(fetchSql, appointmentID);

            int patientID = ((Number) data.get("patient_id")).intValue();
            int doctorID = ((Number) data.get("doctor_id")).intValue();

            String appointmentType = data.get("appointment_type") == null ?
                    "CONSULTATION" : data.get("appointment_type").toString().trim().toUpperCase();
            BigDecimal totalFee = data.get("total_fee") == null ?
                    BigDecimal.ZERO : new BigDecimal(data.get("total_fee").toString()).setScale(2);
            String actualDate = data.get("appt_date").toString();
            String actualTime = data.get("appt_time").toString();
            String logEntry = String.format("%d|%d|%d|%s|%.2f|%s",
                    appointmentID, patientID, doctorID, appointmentType,
                    totalFee.doubleValue(), actualDate + "T" + actualTime);
            apptFileService.logToFile(logEntry);
        } catch (Exception exception) {
            System.err.println("Appointment ID " + appointmentID +
                    " was updated in MySQL, but the appointment " +
                    "file could not be synchronized: " + exception.getMessage());
        }
    }
    private boolean isValidTimeRange(String startTime, String endTime) {
        if (startTime == null || startTime.isBlank() || endTime == null || endTime.isBlank()) {
            return false;
        }
        try {
            return java.time.LocalTime.parse(startTime).isBefore(java.time.LocalTime.parse(endTime));
        } catch (Exception exception) {
            return false;
        }
    }
    private String normalizeAppointmentType(String appointmentType) {
        if (appointmentType == null || appointmentType.isBlank()) {
            return "CONSULTATION";
        }
        String normalizedType = appointmentType.trim().toUpperCase();
        return switch (normalizedType) {
            case "CONSULTATION", "SURGERY", "FOLLOW_UP", "EMERGENCY" -> normalizedType;
            default -> "CONSULTATION";
        };
    }
    private String normalizeStatus(String status) {
        String normalizedStatus = status.trim().toUpperCase();
        return switch (normalizedStatus) {
            case "PENDING", "CONFIRMED", "RESCHEDULED", "COMPLETED", "CANCELLED" -> normalizedStatus;
            default -> "PENDING";
        };
    }
}