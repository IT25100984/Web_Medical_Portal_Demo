package com.webmedicalportaldemo.dao;

import com.webmedicalportaldemo.model.EmergencyRequest;
import com.webmedicalportaldemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmergencyDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmergencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<EmergencyRequest> emergencyRowMapper = (rs, rowNum) -> {
        EmergencyRequest req = new EmergencyRequest();
        req.setRequestId(rs.getInt("request_id"));
        req.setPatientName(rs.getString("patient_name"));
        req.setContactNumber(rs.getString("contact_number"));
        req.setLocation(rs.getString("location"));
        req.setDescription(rs.getString("description"));
        req.setStatus(rs.getString("status"));

        req.setPriorityLevel(rs.getString("priority_level"));
        req.setEmergencyContact(rs.getString("emergency_contact"));
        req.setRequiresAmbulance(rs.getBoolean("requires_ambulance"));

        req.setAssignedDoctorId(rs.getObject("assigned_doctor_id", Integer.class));
        req.setAssignedDoctorName(rs.getString("doctor_name"));
        req.setCreatedAt(rs.getTimestamp("created_at"));
        return req;
    };

    // 1. Fetch all active emergencies for the Admin Dashboard
    public List<EmergencyRequest> getActiveEmergencies() {
        String sql = "SELECT e.*, " +
                "CONCAT('Dr. ', u.first_name, ' ', u.last_name) AS doctor_name " +
                "FROM emergency_requests e " +
                "LEFT JOIN users u ON e.assigned_doctor_id = u.user_id " +
                "WHERE e.status IN ('PENDING', 'ASSIGNED') " +
                "ORDER BY e.created_at DESC";
        return jdbcTemplate.query(sql, emergencyRowMapper);
    }

    // 2. Fetch doctors who are currently AVAILABLE and not on an active assignment
    public List<User> getAvailableDoctors() {
        String sql = "SELECT * FROM users " +
                "WHERE role = 'DOCTOR' " +
                "AND availability_status = 'AVAILABLE' " +
                "AND user_id NOT IN (" +
                "   SELECT assigned_doctor_id FROM emergency_requests " +
                "   WHERE status = 'ASSIGNED' AND assigned_doctor_id IS NOT NULL" +
                ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User doc = new User();
            doc.setuserID(rs.getInt("user_id"));
            doc.setFirstName(rs.getString("first_name"));
            doc.setLastName(rs.getString("last_name"));
            return doc;
        });
    }

    // 3. Create a new emergency request
    public boolean createEmergencyRequest(EmergencyRequest req) {
        String sql = "INSERT INTO emergency_requests " +
                "(patient_name, contact_number, location, description, priority_level, emergency_contact, requires_ambulance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                req.getPatientName(),
                req.getContactNumber(),
                req.getLocation(),
                req.getDescription(),
                req.getPriorityLevel(),
                req.getEmergencyContact(),
                req.isRequiresAmbulance()
        ) > 0;
    }

    // 4. Assign a doctor to an emergency (Admin action)
    public boolean assignDoctor(int requestId, int doctorId) {
        String sql = "UPDATE emergency_requests SET assigned_doctor_id = ?, status = 'ASSIGNED' WHERE request_id = ?";

        int rowsAffected = jdbcTemplate.update(sql, doctorId, requestId);

        if (rowsAffected > 0) {
            jdbcTemplate.update("UPDATE users SET availability_status = 'BUSY' WHERE user_id = ?", doctorId);
            return true;
        }
        return false;
    }

    // 5. Fetch active emergency assignment for a specific doctor
    public EmergencyRequest getActiveAssignmentByDoctorId(int doctorId) {
        String sql = "SELECT e.*, " +
                "CONCAT('Dr. ', u.first_name, ' ', u.last_name) AS doctor_name " +
                "FROM emergency_requests e " +
                "LEFT JOIN users u ON e.assigned_doctor_id = u.user_id " +
                "WHERE e.assigned_doctor_id = ? AND e.status = 'ASSIGNED' " +
                "ORDER BY e.created_at DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, emergencyRowMapper, doctorId);
        } catch (EmptyResultDataAccessException e) {
            return null; // Return null if doctor has no active assignment
        }
    }

    // 6. Complete an emergency assignment & mark doctor AVAILABLE again
    public boolean completeEmergency(int requestId, int doctorId, String notes) {
        String sql = "UPDATE emergency_requests " +
                "SET status = 'RESOLVED', description = CONCAT(IFNULL(description, ''), ' | Clinical Notes: ', ?) " +
                "WHERE request_id = ? AND assigned_doctor_id = ?";

        int rowsAffected = jdbcTemplate.update(sql, notes, requestId, doctorId);

        if (rowsAffected > 0) {
            // Set doctor's availability_status back to AVAILABLE
            jdbcTemplate.update("UPDATE users SET availability_status = 'AVAILABLE' WHERE user_id = ?", doctorId);
            return true;
        }
        return false;
    }
}